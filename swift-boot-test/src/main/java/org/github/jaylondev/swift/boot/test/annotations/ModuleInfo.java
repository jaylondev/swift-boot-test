package org.github.jaylondev.swift.boot.test.annotations;

import java.lang.annotation.*;

/**
 * 多module工程设置
 * @author jaylon 2023/10/22 16:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ModuleInfo {

    /**
     * 测试类所在的module名，当relateModules不为空时必填
     */
    String testModule() default "";

    /**
     * 关联module名称，多个以英文逗号分隔
     * 用于收集关联module的target目录下class列表，当测试目标类注入接口字段时可在列表找到接口的所有实现类
     */
    String[] relateModules() default {};
}
