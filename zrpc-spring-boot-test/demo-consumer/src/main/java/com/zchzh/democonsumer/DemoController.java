package com.zchzh.democonsumer;

import com.zchzh.demoapi.service.DemoService;
import com.zchzh.zrpcstarter.annotation.ZReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Slf4j
@RestController
public class DemoController {

    @ZReference
    private DemoService demoService;


    @RequestMapping("/get")
    public String getMsg(){
        String s = demoService.getMsg();
        log.info(s);
        return s;
    }

    @RequestMapping("/send")
    public String sendMsg(@RequestParam("msg") String msg){
        return demoService.sendMsg(msg);
    }

}
