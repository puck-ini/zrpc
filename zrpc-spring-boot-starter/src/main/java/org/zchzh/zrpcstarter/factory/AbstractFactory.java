package org.zchzh.zrpcstarter.factory;

import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.exception.CommonException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/7/23
 */
public abstract class AbstractFactory<T> {

    private final Map<String, T> MAP = new ConcurrentHashMap<>();

    /**
     * 获取 spi 加载的类的类型
     * @return 返回加载的类的类型
     */
    protected abstract Class<T> getType();

    public T getInstance(String name) {
        return MAP.computeIfAbsent(name, k -> get(name));
    }

    protected T get(String name) {
        ServiceLoader<T> loader = ServiceLoader.load(getType());
        for (T t : loader) {
            JdkSPI jdkspi = t.getClass().getAnnotation(JdkSPI.class);
            if (Objects.isNull(jdkspi)) {
                throw new IllegalArgumentException("name can not be empty");
            }
            if (Objects.equals(name, jdkspi.value())) {
                return t;
            }
        }
        throw new CommonException("invalid spi config");
    }



}
