package org.zchzh.zrpcstarter.remote.handler;

import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.remote.server.ServerServiceCache;
import org.zchzh.zrpcstarter.util.ServiceUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<ZRpcRequest> {

    private Map<String, Object> serviceMap;

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
                    thread.setName("NettyServerHandler-" + r.hashCode());
                    return thread;
                }
            });


    public RequestHandler() {};
    public RequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcRequest req) throws Exception {
        if (req.getRequestId().startsWith(Constants.PING)) {
            log.info("ping >>>>>>>> server - {}, reqId {} ", new Date(), req.getRequestId());
            return;
        }
        pool.execute(() -> {

            ZRpcResponse response = new ZRpcResponse();
            response.setRequestId(req.getRequestId());
            try {
                Object result = handle(req);
                response.setResult(result);
            }catch (Throwable t) {
                response.setError(t.toString());
            }
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("send response for request" + req.getRequestId());
                }
            });
        });
    }

    /**
     * 通过 cglib 获取对应的service处理请求
     * @param request
     * @return
     */
    private Object handle(ZRpcRequest request) throws InvocationTargetException {
        if (request.getRequestId().startsWith(Constants.PING)) {
            return null;
        }
        String interfaceName = request.getClassName();
        Object obj = ServerServiceCache.get(interfaceName);
        if (Objects.isNull(obj)) {
            throw new RuntimeException("can not find service implement with interface name " + interfaceName);
        }
        Class<?> serviceClass = obj.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

//        String className = request.getClassName();
//        String version = request.getVersion();
//        String serviceKey = ServiceUtil.makeServiceKey(className, version);
//        Object serviceBean = serviceMap.get(serviceKey);
//        if (serviceBean == null) {
//            log.error("Can not find service implement with interface name: {} and version: {}", className, version);
//            return null;
//        }
//
//        Class<?> serviceClass = serviceBean.getClass();
//        String methodName = request.getMethodName();
//        Class<?>[] parameterTypes = request.getParameterTypes();
//        Object[] parameters = request.getParameters();

        // cglib reflect
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName,parameterTypes);
        return fastClass.invoke(methodIndex, obj, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("NettyServerHandler exceptionCaught", cause);
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