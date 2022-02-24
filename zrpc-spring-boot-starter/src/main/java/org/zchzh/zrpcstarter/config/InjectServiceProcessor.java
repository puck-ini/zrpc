package org.zchzh.zrpcstarter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.zchzh.zrpcstarter.annotation.ZReference;
import org.zchzh.zrpcstarter.proxy.InvokeProxy;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author zengchzh
 * @date 2021/7/9
 *
 *
 */
@Slf4j
public class InjectServiceProcessor implements ApplicationListener<ContextRefreshedEvent> /*BeanFactoryPostProcessor*/ {

    @Resource
    private InvokeProxy invokeProxy;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            injectService(context);
        }
    }

    private void injectService(ApplicationContext context) {
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            Class<?> clazz = context.getType(name);
            if (Objects.isNull(clazz)) {
                continue;
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ZReference reference = field.getAnnotation(ZReference.class);
                if (Objects.isNull(reference)) {
                    continue;
                }
                Class<?> fieldClass = field.getType();
                Object obj = context.getBean(name);
                field.setAccessible(true);
                try {
                    field.set(obj, invokeProxy.getProxy(fieldClass, reference.loadBalance()));
                } catch (IllegalAccessException e) {
                    log.error("inject service fail - ", e);
                    throw new RuntimeException("inject service fail");
                }
            }
        }
    }
}
