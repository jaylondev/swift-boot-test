package org.github.jaylondev.swift.boot.test.collect.handler;

import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;

import java.util.Arrays;

/**
 * 收集在@SwiftBootTest注解includeComponent属性中标记的类，这些类通常没有在测试目标类的@AutoWired引用链中，但测试方法执行时需要被实例化
 * @author jaylon 2023/9/12 22:39
 */
public class AdditionalClassCollectHandler implements ICollectHandler {

    @Override
    public void collect(CollectContext collectContext) {
        Class<?>[] includeComponentClasses = collectContext.getSwiftBootTest().includeComponent();
        if (includeComponentClasses.length > 0) {
            collectContext.getInjectClassList().addAll(Arrays.asList(includeComponentClasses));
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
