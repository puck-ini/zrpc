package org.zchzh.zrpcstarter.remote.handler;

import io.netty.handler.codec.TooLongFrameException;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.model.RpcProp;
import org.zchzh.zrpcstarter.model.ZRpcMessage;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.remote.server.ServerServiceHolder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<ZRpcMessage> {

    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            12,
            24,
            Constants.BEAT_TIME * 3,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("RequestHandler-" + r.hashCode());
                    return thread;
                }
            });

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcMessage message) throws Exception {
        ZRpcMessage resMsg = ZRpcMessage.builder()
                .messageType(MessageType.RESPONSE)
                .serializerType(RpcProp.INSTANCE.getServer().getServerSerializer())
                .compressType(RpcProp.INSTANCE.getServer().getServerCompress())
                .build();
        if (message.getMessageType() == MessageType.BEAT_RES) {
            resMsg.setMessageType(MessageType.BEAT_RES);
            ctx.channel().writeAndFlush(resMsg);
            return;
        }
        ZRpcRequest req = (ZRpcRequest) message.getData();

        CompletableFuture<Void> handlerFuture = CompletableFuture.runAsync(() -> {
            ZRpcResponse response = new ZRpcResponse();
            response.setRequestId(req.getRequestId());
            try {
                Object result = handle(req);
                response.setResult(result);
            }catch (Throwable t) {
                response.setError(t.toString());
            }
            resMsg.setData(response);
        }, pool);

        handlerFuture.thenAccept(aVoid -> ctx.writeAndFlush(resMsg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("send response for request" + req.getRequestId());
            } else {
                log.error("send response fail with request - {}", req.getRequestId(), future.cause());
            }
        }));
    }

    /**
     * 通过 cglib 获取对应的service处理请求
     * @param request
     * @return
     */
    private Object handle(ZRpcRequest request) throws InvocationTargetException {
        String interfaceName = request.getClassName();
        Object obj = ServerServiceHolder.get(interfaceName);
        if (Objects.isNull(obj)) {
            throw new RuntimeException("can not find service implement with interface name " + interfaceName);
        }
        Class<?> serviceClass = obj.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        // cglib reflect
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName,parameterTypes);
        return fastClass.invoke(methodIndex, obj, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("RequestHandler exceptionCaught", cause);
        if (cause instanceof TooLongFrameException) {
            // 处理请求数据太大问题
            ZRpcMessage message = ZRpcMessage.builder()
                    .messageType(MessageType.HANDLER_REQ_ERROR_REQ)
                    .serializerType(RpcProp.INSTANCE.getServer().getServerSerializer())
                    .compressType(RpcProp.INSTANCE.getServer().getServerCompress())
                    .build();
            ctx.channel().writeAndFlush(message);
        }
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            log.info("Channel idle in last seconds, close it - " + Constants.BEAT_TIME);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
