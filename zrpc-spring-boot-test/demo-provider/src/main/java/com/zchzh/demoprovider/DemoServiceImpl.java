package com.zchzh.demoprovider;

import com.zchzh.demoapi.service.DemoService;
import com.zchzh.zrpcstarter.annotation.ZService;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@ZService
public class DemoServiceImpl implements DemoService {

    @SneakyThrows
    @Override
    public String getMsg() {
        TimeUnit.MILLISECONDS.sleep(100);
        return "getMsg: " + System.currentTimeMillis();
    }

    @Override
    public String sendMsg(String msg) {
        return System.currentTimeMillis() + " sendMsg: " + msg ;
    }
}
