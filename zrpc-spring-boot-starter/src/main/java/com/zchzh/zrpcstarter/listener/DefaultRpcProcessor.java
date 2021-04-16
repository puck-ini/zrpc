package com.zchzh.zrpcstarter.listener;

import com.zchzh.zrpcstarter.annotation.ZReference;
import com.zchzh.zrpcstarter.annotation.ZService;
import com.zchzh.zrpcstarter.properties.ZRpcProperty;
import com.zchzh.zrpcstarter.proxy.ClientProxyFactory;
import com.zchzh.zrpcstarter.server.AbstractServer;
import com.zchzh.zrpcstarter.server.register.ServiceRegister;
import com.zchzh.zrpcstarter.server.register.ServiceObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author zengchzh
 * @date 2021/3/11
 *
 * RPC处理者，支持服务启动暴露、自动注入Service
 * ApplicationListener<ContextRefreshedEvent>：
 * 当Spring中的所有的Bean都加载完成时，Spring会发布一个时间，
 * ApplicationListener<ContextRefreshedEvent>的作用是监听这个事件，当监听到事件发布就会执行onApplicationEvent方法
 */
public class DefaultRpcProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ClientProxyFactory clientProxyFactory;

    @Resource
    private ServiceRegister serviceRegister;

    @Resource
    private AbstractServer server;

    private ZRpcProperty property;


    public DefaultRpcProcessor() {}

    public DefaultRpcProcessor(ZRpcProperty property) {
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
                    if (interfaces.length != 1){
                        ZService service = clazz.getAnnotation(ZService.class);
                        String value = service.value();
                        if (value.equals("")){
                            startServerFlag = false;
                            throw new UnsupportedOperationException("The exposed interface is not specific with '"
                                    + obj.getClass().getName() + "'");
                        }
                        serviceObject = new ServiceObject(value, Class.forName(value), obj);
                    } else {
                        Class<?> superClass = interfaces[0];
                        serviceObject = new ServiceObject(superClass.getName(), superClass, obj);
                        serviceObject.setPath(superClass.getName());
                    }
                    serviceRegister.register(serviceObject);
                    server.addService(serviceObject.getName(),serviceObject.getObj());
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
                try {
                    field.set(object, clientProxyFactory.getProxy(fieldClass));
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
