package org.zchzh.zrpcstarter.register;

import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.exception.CommonException;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/7/21
 */
public class RegisterFactory {

    private static final Map<String, Register> REGISTER_MAP = new ConcurrentHashMap<>();

    public static Register getInstance(String name) {
        return REGISTER_MAP.computeIfAbsent(name, k -> get(name));
    }

    private static Register get(String name) {
        ServiceLoader<Register> loader = ServiceLoader.load(Register.class);
        for (Register register : loader) {
            JdkSPI jdkSPI = register.getClass().getAnnotation(JdkSPI.class);
            if (Objects.isNull(jdkSPI)) {
                throw new IllegalArgumentException("register name can not be empty");
            }
            String[] arr = name.split(":");
            if (Objects.equals(arr[0], jdkSPI.value())) {
                return register.init(arr[1]);
            }
        }
        throw new CommonException("invalid register config");
    }
}
