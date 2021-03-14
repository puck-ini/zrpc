package com.zchzh.demoprovider;

import com.zchzh.demoapi.service.DemoService;
import com.zchzh.zrpcstarter.annotation.ZService;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@ZService
public class DemoServiceImpl implements DemoService {

    @Override
    public String getMsg() {
        return "getMsg: " + System.currentTimeMillis();
    }

    @Override
    public String sendMsg(String msg) {
        return System.currentTimeMillis() + " sendMsg: " + msg ;
    }
}
