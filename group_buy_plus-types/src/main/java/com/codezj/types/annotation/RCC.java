package com.codezj.types.annotation;

import java.lang.annotation.*;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 使用Redis实现的动态配置中心注解，Redis config center
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface RCC {

    String value() default "";
}
