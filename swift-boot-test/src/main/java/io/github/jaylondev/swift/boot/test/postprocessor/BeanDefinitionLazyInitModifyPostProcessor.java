package io.github.jaylondev.swift.boot.test.postprocessor;

import lombok.extern.slf4j.Slf4j;
import io.github.jaylondev.swift.boot.test.constants.TestClassContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 修改IOC容器中类的BeanDefinition对象懒加载属性
 * @author jaylon 2023/9/12 22:17
 */
@Slf4j
public class BeanDefinitionLazyInitModifyPostProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * 将IOC中所有的类设置为懒加载模式
     */
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
            log.warn("[SwiftBootTest] beanDefinition get faild! beanName:{}", beanName);
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
