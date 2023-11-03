package org.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

/**
 * @author jaylon 2023/10/22 16:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ModuleInfo {

    String testModule() default "";

    String[] relateModules() default {};
}
