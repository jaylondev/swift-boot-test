package io.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

/**
 * @author jaylon 2023/11/14 17:46
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbTestEnvironment {
    /**
     * mapper包路径
     * @return mapper包路径
     */
    String[] mapperBasePackages() default {};
    /**
     * mapper.xml文件路径
     * @return mapper.xml文件路径
     */
    String mapperXmlLocation() default "";
}
