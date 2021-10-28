package org.zchzh.zrpcstarter.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.exception.CommonException;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.client.Client;
import org.zchzh.zrpcstarter.remote.client.ClientHolder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * @author zengchzh
 * @date 2021/8/5
 */
@Slf4j
public abstract class AbstractInvocationHandler {

    protected final Class<?> clazz;

    private final Register discovery;

    private final LoadBalance loadBalance;

    public AbstractInvocationHandler(Class<?> clazz, Register discovery, LoadBalance loadBalance) {
        this.clazz = clazz;
        this.discovery = discovery;
        this.loadBalance = loadBalance;
    }

    protected Object handler(Object o, Method method, Object[] args) throws ExecutionException, InterruptedException {
        String serviceName = clazz.getName();
        ZRpcRequest request = ZRpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .className(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        ServiceObject so = getServiceObject(serviceName);
        return getResult(ClientHolder.get(so), request);
    }

    private Object getResult(Client client, ZRpcRequest request) throws ExecutionException, InterruptedException {
        CompletableFuture<Object> resultFuture = client.invoke(request)
                .thenApply(new Function<ZRpcResponse, Object>() {
                    @Override
                    public Object apply(ZRpcResponse response) {
                        if (response.isError()) {
                            throw new CommonException(response.getError());
                        }
                        return response.getResult();
                    }
                }).exceptionally(new Function<Throwable, Object>() {
                    @Override
                    public Object apply(Throwable throwable) {
                        return throwable;
                    }
                });
        if (resultFuture.isCompletedExceptionally()) {
            throw new CommonException(((Throwable) resultFuture.get()).getMessage());
        }
        return resultFuture.get();
    }

    private ServiceObject getServiceObject(String serviceName) {
        List<ServiceObject> serviceObjectList = discovery.getAll(serviceName);
        if (CollectionUtils.isEmpty(serviceObjectList)) {
            throw new CommonException("can not find service with name : " + serviceName);
        }
        return loadBalance.get(serviceObjectList);
    }
}
