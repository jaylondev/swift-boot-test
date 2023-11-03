package io.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;


/**
 * 单元测试目标类没有直接或间接引用，但测试方法执行中需要用到的类，使用此注解标记
 * 建议标记在单元测试基类上，用于标记项目中大部分测试类都需要加载的类(比如SpringContextUtils、RedisLockUtils等)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IncludeComponent {

    Class<?>[] value() default {};
}
