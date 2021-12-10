package org.zchzh.zrpcstarter.model;

import lombok.Data;
import org.zchzh.zrpcstarter.exception.CommonException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * @author zengchzh
 * @date 2021/12/10
 */

@Data
public class PendingRequest {

    private String requestId;

    private ZRpcRequest request;

    private CompletableFuture<ZRpcResponse> resFuture;

    private CompletableFuture<Object> resultFuture;

    private long invokeTime;

    public PendingRequest(ZRpcRequest request) {
        this.requestId = request.getRequestId();
        this.request = request;
        this.resFuture  = new CompletableFuture<>();
        this.resultFuture = this.resFuture.thenApply(new Function<ZRpcResponse, Object>() {
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
        this.invokeTime = System.currentTimeMillis();
    }


    public void complete(ZRpcResponse res) {
        resFuture.complete(res);
    }


    public Object getResult() {
        try {
            if (resultFuture.isCompletedExceptionally()) {
                throw new CommonException(((Throwable) resultFuture.get()).getMessage());
            }
            return resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new CommonException("获取结果失败 requestId : [" + requestId + "]", e);
        }
    }
}
