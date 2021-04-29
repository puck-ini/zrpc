package com.zchzh.zrpcstarter.listener;

import com.zchzh.zrpcstarter.annotation.ZReference;
import com.zchzh.zrpcstarter.annotation.ZService;
import com.zchzh.zrpcstarter.cache.ClientCache;
import com.zchzh.zrpcstarter.client.TestClient;
import com.zchzh.zrpcstarter.discovery.ServiceDiscover;
import com.zchzh.zrpcstarter.properties.ZRpcProperty;
import com.zchzh.zrpcstarter.proxy.ClientProxyFactory;
import com.zchzh.zrpcstarter.register.ServiceRegister;
import com.zchzh.zrpcstarter.protocol.service.ServiceObject;
import com.zchzh.zrpcstarter.server.Server;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/3/11
 *
 * RPC处理者，支持服务启动暴露、自动注入Service
 */

@Slf4j
public class DefaultRpcProcessorListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ClientProxyFactory clientProxyFactory;

    @Resource
    private ServiceRegister serviceRegister;

    @Resource
    private ServiceDiscover serviceDiscover;

    @Resource
    private Server server;

    private Timeout timeout;

    private final List<String> serverNameList = new ArrayList<>();

    private ZRpcProperty property;


    public DefaultRpcProcessorListener() {}

    public DefaultRpcProcessorListener(ZRpcProperty property) {
        this.property = property;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (Objects.isNull(contextRefreshedEvent.getApplicationContext().getParent())) {
            ApplicationContext context = contextRefreshedEvent.getApplicationContext();
            // 开启服务
            startServer(context);

            // 注入Service
            injectService(context);

            // 准备client
            prepareClient();

        }
    }

    /**
     * 注册所有服务
     * @param context
     */
    private void startServer(ApplicationContext context){
        Map<String, Object> beans = context.getBeansWithAnnotation(ZService.class);

        if (beans.size() != 0){
            boolean startServerFlag = true;
            for (Object obj: beans.values()){
                try {
                    Class<?> clazz = obj.getClass();
                    Class<?>[] interfaces = clazz.getInterfaces();
                    Method[] methods = clazz.getDeclaredMethods();
                    ServiceObject serviceObject;
                    String interfacesName = null;
                    if (interfaces.length != 1){
                        ZService service = clazz.getAnnotation(ZService.class);
                        String value = service.value();
                        if (value.equals("")){
                            startServerFlag = false;
                            throw new UnsupportedOperationException("The exposed interface is not specific with '"
                                    + obj.getClass().getName() + "'");
                        }
                        serviceObject = new ServiceObject(value);
                    } else {
                        Class<?> superClass = interfaces[0];
                        interfacesName = superClass.getName();
                        serviceObject = new ServiceObject(interfacesName);
                        serviceObject.setPath(interfacesName);
                        serviceObject.setClassName(obj.getClass().getName());
                    }
                    log.info("ServiceObject>>>>>>>>>>>>>" + serviceObject.toString());
                    serviceRegister.register(serviceObject);
                    server.addService(interfacesName,obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (startServerFlag){
                server.start();
            }
        }
    }

    /**
     * 注入服务
     * @param context
     */
    private void injectService(ApplicationContext context) {
        String[] names = context.getBeanDefinitionNames();
        for (String name: names) {
            Class<?> clazz = context.getType(name);
            if (Objects.isNull(clazz)) {
                continue;
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field: fields) {
                ZReference zReference = field.getAnnotation(ZReference.class);
                if (Objects.isNull(zReference)){
                    continue;
                }
                Class<?> fieldClass = field.getType();
                Object object = context.getBean(name);
                field.setAccessible(true);
                serverNameList.add(fieldClass.getName());
                try {
                    field.set(object, clientProxyFactory.getProxy(fieldClass));
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void prepareClient() {
        timeout = new HashedWheelTimer().newTimeout(to -> {
            serverNameList.forEach(s -> {
                serviceDiscover.getService(s).forEach(so -> {
                    String[] strings = so.getAddress().split(":");
                    ClientCache.MAP.put(ClientCache.MAP.makeKey(so),
                            new TestClient(strings[0], Integer.parseInt(strings[1])));
                });
            });
        }, 5, TimeUnit.SECONDS);

        Executors.defaultThreadFactory().newThread(() -> {
            try {
                TimeUnit.SECONDS.sleep(31);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout.cancel();
        }).start();
    }


}
