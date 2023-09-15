package org.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IncludeComponent {

    Class<?>[] value() default {};
}
