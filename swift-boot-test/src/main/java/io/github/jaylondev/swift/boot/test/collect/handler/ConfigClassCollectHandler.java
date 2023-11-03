package io.github.jaylondev.swift.boot.test.collect.handler;

import io.github.jaylondev.swift.boot.test.collect.CollectContext;
import io.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import io.github.jaylondev.swift.boot.test.postprocessor.BeanDefinitionLazyInitModifyPostProcessor;

import java.util.Objects;
import java.util.Set;

/**
 * 配置类收集器
 * @author jaylon 2023/8/7 22:41
 */
public class ConfigClassCollectHandler implements ICollectHandler {

    @Override
    public void collect(CollectContext collectContext) {
        Set<Class<?>> classListContailer = collectContext.getInjectClassList();
        classListContailer.add(BeanDefinitionLazyInitModifyPostProcessor.class);
        if (!Objects.equals(Void.class, collectContext.getTargetClass())) {
            classListContailer.add(collectContext.getTargetClass());
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
