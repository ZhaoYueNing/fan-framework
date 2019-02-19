package cn.zynworld.fan.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhaoyuening on 2019/2/19.
 * 属性赋值
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    /**
     * 属性名
     */
    String name() default "";
}
