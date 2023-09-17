package org.github.jaylondev.swift.boot.test.collect.handler;

import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import org.github.jaylondev.swift.boot.test.config.DBTestConfig;
import org.github.jaylondev.swift.boot.test.config.RedisTestConfig;
import org.github.jaylondev.swift.boot.test.postprocessor.BeanDefinitionLazyInitModifyPostProcessor;

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
        boolean connectDataBase = collectContext.getSwiftBootTest().connectDataBase();
        boolean connectRedis = collectContext.getSwiftBootTest().connectRedis();
        if (connectRedis) {
            classListContailer.add(RedisTestConfig.class);
        }
        if (connectDataBase) {
            classListContailer.add(DBTestConfig.class);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
