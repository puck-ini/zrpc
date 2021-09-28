package com.zchzh.democonsumer;

import com.zchzh.demoapi.service.DemoService;
import com.zchzh.demoapi.service.model.TestDTO;
import org.zchzh.zrpcstarter.annotation.ZReference;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Slf4j
@RestController
public class DemoController {

    @ZReference
//    @Reference
    private DemoService demoService;


    @RequestMapping("/v1/get")
    public String getMsg() {
        String s = demoService.getMsg();
        log.info(s);
        return s;
    }

    @RequestMapping("/v2/get/{sleep}")
    public String getMsg(@PathVariable("sleep") long sleep) {
        String s = demoService.getMsg(sleep);
        log.info(s);
        return s;
    }

    @RequestMapping("/send")
    public String sendMsg(@RequestParam("msg") String msg){
        return demoService.sendMsg(msg);
    }


    @RequestMapping("/senddto")
    public TestDTO sendDto() {
        return demoService.sendDto(new TestDTO());
    }

    @RequestMapping("/sendlist/{loop}")
    public List<TestDTO> sendList(@PathVariable("loop") int loop) {
        List<TestDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            dtoList.add(new TestDTO());
        }
        return demoService.sendList(dtoList);
    }

    @RequestMapping("/sendfuture")
    public TestDTO sendFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<TestDTO> future = demoService.sendFuture(new TestDTO());
        future.whenComplete(new BiConsumer<TestDTO, Throwable>() {
            @Override
            public void accept(TestDTO dto, Throwable throwable) {
                log.info(dto.getMsg());
            }
        });
        return future.get();
    }

}
