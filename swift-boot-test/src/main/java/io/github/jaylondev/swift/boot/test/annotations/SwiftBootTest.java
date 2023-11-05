package io.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

/**
 * 用于标记单元测试类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SwiftBootTest {
    /**
     * @return 测试目标类
     */
    Class<?> targetClass() default Void.class;
    /**
     * @return 目标类没有直接或间接注入但测试方法执行时需要实例化的类
     */
    Class<?>[] includeComponent() default {};
    /**
     * @return 无需声明为懒加载的类
     */
    Class<?>[] unLazyClasses() default {};
}
