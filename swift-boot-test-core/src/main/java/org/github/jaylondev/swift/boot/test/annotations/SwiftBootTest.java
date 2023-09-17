package org.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SwiftBootTest {
    /**
     * 测试目标类
     */
    Class<?> targetClass() default Void.class;
    /**
     * 目标类没有直接或间接注入但测试中需要实例化的类
     */
    Class<?>[] includeComponent() default {};
    /**
     * 不可声明为懒加载的类
     */
    Class<?>[] unLazyClasses() default {};
    /**
     * 是否连接数据库
     */
    boolean connectDataBase() default false;

    /**
     * 是否连接redis
     */
    boolean connectRedis() default false;
}
