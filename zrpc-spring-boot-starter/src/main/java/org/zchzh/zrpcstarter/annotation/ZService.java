package org.zchzh.zrpcstarter.annotation;

import org.springframework.stereotype.Component;
import org.zchzh.zrpcstarter.constants.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zengchzh
 * @date 2021/3/10
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ZService {
    String name() default "";

    double weight() default 1.0;

    String loadBalance() default Constants.RANDOM;
}
