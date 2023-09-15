package org.github.jaylondev.swift.boot.test.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.constants.TestClassContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author jaylon 2023/9/12 22:17
 */
@Slf4j
public class BeanDefinitionLazyInitModifyPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Class<?>> preparedModifyLazyInitLasses = TestClassContainer.getPreparedModifyLazyInitClasses();
        preparedModifyLazyInitLasses.remove(getClass());
        for (Class<?> clazz : preparedModifyLazyInitLasses) {
            String uperCaseBeanName = clazz.getSimpleName();
            if (StringUtils.isEmpty(uperCaseBeanName)) {
                continue;
            }
            String lowerCaseBeanName = this.toLowerCaseFirstLetter(uperCaseBeanName);
            if (!this.modifyLazyInitAttribute(registry, lowerCaseBeanName)) {
                modifyLazyInitAttribute(registry, uperCaseBeanName);
            }
        }
    }

    private boolean modifyLazyInitAttribute(BeanDefinitionRegistry registry, String beanName) {
        BeanDefinition beanDefinition = this.getMergedDefinition(registry, beanName);
        if (beanDefinition != null) {
            beanDefinition.setLazyInit(true);
            registry.registerBeanDefinition(beanName, beanDefinition);
            log.debug("[SwiftBootTest] {}'s lazyInit attribute has been set to true", beanName);
            return true;
        }
        return false;
    }

    private BeanDefinition getMergedDefinition(BeanDefinitionRegistry registry, String beanName) {
        BeanDefinition beanDefinition = null;
        try {
            beanDefinition = ((DefaultListableBeanFactory) registry).getMergedBeanDefinition(beanName);
        } catch (Exception e) {
            log.warn("[SwiftBootTest] beanDefinition get faild! beanName:{} exception:", beanName, e);
        }
        return beanDefinition;
    }

    private String toLowerCaseFirstLetter(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
