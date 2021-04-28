package com.zchzh.zrpcstarter.server;

import com.zchzh.zrpcstarter.config.Constants;
import com.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import com.zchzh.zrpcstarter.util.ServiceUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<ZRpcRequest> {

    private final Map<String, Object> serviceMap;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            12,
            24,
            Constants.BEAT_TIME * 3,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
//                    thread.setDaemon(true);
                    thread.setName("NettyServerHandler-" + r.hashCode());
                    return thread;
                }
            });


    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcRequest request) throws Exception {
        if (request.getRequestId().startsWith(Constants.PING)) {
            log.info("ping --------------------" + new Date());
            return;
        }
        threadPoolExecutor.execute(() -> {

            ZRpcResponse response = new ZRpcResponse();
            response.setRequestId(request.getRequestId());
            try {
                Object result = handle(request);
                response.setResult(result);
            }catch (Throwable t) {
                response.setError(t.toString());
            }
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("send response for request" + request.getRequestId());
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
        String className = request.getClassName();
        String version = request.getVersion();
        String serviceKey = ServiceUtil.makeServiceKey(className, version);
        Object serviceBean = serviceMap.get(serviceKey);
        if (serviceBean == null) {
            log.error("Can not find service implement with interface name: {} and version: {}", className, version);
            return null;
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        // cglib reflect
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName,parameterTypes);
        return fastClass.invoke(methodIndex, serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("NettyServerHandler exceptionCaught");
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
