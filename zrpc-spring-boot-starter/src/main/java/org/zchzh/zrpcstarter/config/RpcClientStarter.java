package org.zchzh.zrpcstarter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.zchzh.zrpcstarter.annotation.ZReference;
import org.zchzh.zrpcstarter.proxy.ProxyFactory;

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
public class RpcClientStarter implements ApplicationListener<ContextRefreshedEvent> /*BeanFactoryPostProcessor*/ {

    @Resource
    private ProxyFactory proxyFactory;

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
                    field.set(obj, proxyFactory.getProxy(fieldClass));
                } catch (IllegalAccessException e) {
                    log.error("inject service fail - ", e);
                    throw new RuntimeException("inject service fail");
                }
            }
        }
    }

//    private BeanFactory beanFactory;
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        this.beanFactory = beanFactory;
//        postProcessRpcClientBeanFactory(beanFactory, (BeanDefinitionRegistry) beanFactory);
//    }
//
//    private void postProcessRpcClientBeanFactory(ConfigurableListableBeanFactory beanFactory,
//                                                 BeanDefinitionRegistry beanDefinitionRegistry) {
//        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
//        int len = beanDefinitionNames.length;
//        for (int i = 0; i < len; i++) {
//            String beanDefinitionName = beanDefinitionNames[i];
//            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
//            String beanCLassName = beanDefinition.getBeanClassName();
//            if (Objects.nonNull(beanCLassName)) {
//                Class<?> clazz = ClassUtils.resolveClassName(beanCLassName, null);
//                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
//                    @Override
//                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//                        parseField(field);
//                    }
//                });
//            }
//        }
//    }
//
//    private void parseField(Field field) {
//        Reference reference = field.getAnnotation(Reference.class);
//        if (Objects.isNull(reference)) {
//
//        }
//    }


}
