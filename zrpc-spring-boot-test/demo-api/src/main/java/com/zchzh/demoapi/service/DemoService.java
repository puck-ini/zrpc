package com.zchzh.demoapi.service;

import com.zchzh.demoapi.service.model.TestDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public interface DemoService {

    String getMsg();

    String getMsg(long sleep);

    String sendMsg(String msg);

    TestDTO sendDto(TestDTO dto);

    List<TestDTO> sendList(List<TestDTO> dtoList);

    CompletableFuture<TestDTO> sendFuture(TestDTO dto);
}
