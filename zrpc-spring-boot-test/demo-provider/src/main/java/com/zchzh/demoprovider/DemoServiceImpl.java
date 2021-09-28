package com.zchzh.demoprovider;

import com.zchzh.demoapi.service.DemoService;
import com.zchzh.demoapi.service.model.TestDTO;
import org.zchzh.zrpcstarter.annotation.ZService;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

//@Service
@ZService
public class DemoServiceImpl implements DemoService {

    @SneakyThrows
    @Override
    public String getMsg() {
//        TimeUnit.MILLISECONDS.sleep(100);
        return "getMsg: " + System.currentTimeMillis();
    }

    @SneakyThrows
    @Override
    public String getMsg(long sleep) {
        TimeUnit.MILLISECONDS.sleep(sleep);
        return "getMsg: " + System.currentTimeMillis() + " - sleep - " + sleep;
    }

    @Override
    public String sendMsg(String msg) {
        return System.currentTimeMillis() + " sendMsg: " + msg ;
    }

    @Override
    public TestDTO sendDto(TestDTO dto) {
        return dto;
    }

    @Override
    public List<TestDTO> sendList(List<TestDTO> dtoList) {
        return dtoList;
    }

    @Override
    public CompletableFuture<TestDTO> sendFuture(TestDTO dto) {
        return CompletableFuture.supplyAsync(() -> dto);
    }


}
