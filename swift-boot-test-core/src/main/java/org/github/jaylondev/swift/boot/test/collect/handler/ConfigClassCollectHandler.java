package org.github.jaylondev.swift.boot.test.collect.handler;

import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import org.github.jaylondev.swift.boot.test.config.Configurations;
import org.github.jaylondev.swift.boot.test.config.DaoTestConfig;
import org.github.jaylondev.swift.boot.test.config.RedisTestConfig;
import org.github.jaylondev.swift.boot.test.postprocessor.BeanDefinitionLazyInitModifyPostProcessor;

import java.util.Objects;
import java.util.Set;

/**
 * 配置类
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
        Configurations configurations = Configurations.getInstance();
        if (configurations.isConnectRedis()) {
            classListContailer.add(RedisTestConfig.class);
        }
        if (configurations.isConnectDataBase()) {
            classListContailer.add(DaoTestConfig.class);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
