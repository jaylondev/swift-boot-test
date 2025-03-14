package io.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreSpy {

    Class<?>[] fields() default {};
    Class<?>[] classes() default {};

    String[] classSuffixs() default {};

}
