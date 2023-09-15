package org.github.jaylondev.swift.boot.test.collect.handler;

import org.github.jaylondev.swift.boot.test.annotations.IncludeComponent;
import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;

import java.util.Arrays;

/**
 * @author jaylon 2023/9/12 22:36
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
