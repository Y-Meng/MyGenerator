package com.mengcy.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mengcy
 * @date 2019/4/25
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataId{
    String name();
    DataType type();
    int length() default 0;
    String comment() default "";
    boolean autoIncrease() default true;
    boolean unsign() default true;
}
