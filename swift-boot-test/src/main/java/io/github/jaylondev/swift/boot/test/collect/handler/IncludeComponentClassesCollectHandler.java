package io.github.jaylondev.swift.boot.test.collect.handler;

import io.github.jaylondev.swift.boot.test.annotations.IncludeComponent;
import io.github.jaylondev.swift.boot.test.collect.CollectContext;
import io.github.jaylondev.swift.boot.test.collect.ICollectHandler;

import java.util.Arrays;

/**
 * 收集单元测试目标类没有直接或间接引用，但测试方法执行中需要用到的类
 * 比如静态工具类
 */
public class IncludeComponentClassesCollectHandler implements ICollectHandler {

    @Override
    public void collect(CollectContext collectContext) {
        IncludeComponent includeComponent = collectContext.getTestClass().getAnnotation(IncludeComponent.class);
        if (includeComponent != null && includeComponent.value().length > 0) {
            collectContext.getInjectClassList().addAll(Arrays.asList(includeComponent.value()));
        }
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
