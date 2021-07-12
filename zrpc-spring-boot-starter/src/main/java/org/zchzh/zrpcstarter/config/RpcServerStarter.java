package org.zchzh.zrpcstarter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.zchzh.zrpcstarter.annotation.ZService;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.server.Server;
import org.zchzh.zrpcstarter.remote.server.ServerServiceCache;
import org.zchzh.zrpcstarter.util.ServerUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author zengchzh
 * @date 2021/7/9
 */
@Slf4j
public class RpcServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

    @Resource
    private Register register;

    @Resource
    private Server server;

    @Resource
    private RpcProperties rpcProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            registerService(context);
        }
    }


    private void registerService(ApplicationContext context) {
        Map<String, Object> beanMap = context.getBeansWithAnnotation(ZService.class);
        if (beanMap.size() > 0) {
            for (Object obj : beanMap.values()) {
                Class<?> clazz = obj.getClass();
                Class<?>[] interfaces = clazz.getInterfaces();
                String interfaceName;
                ZService service = clazz.getAnnotation(ZService.class);
                if (interfaces.length != 1) {
                    interfaceName = service.name();
                } else {
                    interfaceName = interfaces[0].getName();
                }
                ServiceObject serviceObject = ServiceObject.builder()
                        .serviceName(interfaceName)
                        .ip(ServerUtil.getHost())
                        .port(rpcProperties.getServerPort())
                        .weight(service.weight())
                        .clazz(clazz)
                        .meta(new HashMap<>(10))
                        .build();
                serviceObject.getMeta().put(Constants.LOAD_BALANCE, service.loadBalance());
                register.register(serviceObject);
                ServerServiceCache.put(interfaceName, obj);
            }
            // 启动服务监听请求
            threadFactory.newThread(() -> server.start()).start();
        }
    }
}
