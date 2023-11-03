package io.github.jaylondev.swift.boot.test.collect;

import lombok.Data;
import io.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;

import java.util.Set;

/**
 * @author jaylon 2023/8/7 22:22
 */
@Data
public class CollectContext {

    /**
     * 单元测试需要加载的类集合
     */
    private final Set<Class<?>> injectClassList;
    /**
     * 测试类
     */
    private Class<?> testClass;
    /**
     * 测试目标类
     */
    private Class<?> targetClass;
    /**
     * 测试类上标记的注解
     */
    private SwiftBootTest swiftBootTest;

}
