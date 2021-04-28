package com.zchzh.demoprovider;

import com.zchzh.demoapi.service.DemoService;
import com.zchzh.zrpcstarter.annotation.ZService;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.Service;

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



}
