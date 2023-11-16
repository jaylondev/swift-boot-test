package io.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

/**
 * @author jaylon 2023/11/14 22:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbTest {
    /**
     * 初始化sql在/resources文件下的路径
     * @return 初始化sql
     */
    String sqlFile() default "";
}
