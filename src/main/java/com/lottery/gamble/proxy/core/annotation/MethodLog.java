package com.lottery.gamble.proxy.core.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2016/6/30.
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodLog {
    String remark() default "";
    String operType() default "0";
}
