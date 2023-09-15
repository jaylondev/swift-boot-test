package org.github.jaylondev.swift.boot.test.collect.handler;

import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;

import java.util.Arrays;

/**
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
