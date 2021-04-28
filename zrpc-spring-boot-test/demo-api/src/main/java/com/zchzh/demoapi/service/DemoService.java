package com.zchzh.demoapi.service;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public interface DemoService {

    String getMsg();

    String getMsg(long sleep);

    String sendMsg(String msg);
}
