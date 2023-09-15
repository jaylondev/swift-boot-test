package org.github.jaylondev.swift.boot.test.core;

import lombok.SneakyThrows;
import org.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;
import org.github.jaylondev.swift.boot.test.collect.ClassCollectService;
import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.constants.TestClassContainer;
import org.github.jaylondev.swift.boot.test.exception.SwiftBootTestException;
import org.github.jaylondev.swift.boot.test.utils.BeanUtils;
import org.github.jaylondev.swift.boot.test.utils.JavasistUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.web.WebTestContextBootstrapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SwiftBootTestBootstrapper extends WebTestContextBootstrapper {

    @Override
    protected MergedContextConfiguration processMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        MergedContextConfiguration mcc = super.processMergedContextConfiguration(mergedConfig);
        Class<?> testClass = mergedConfig.getTestClass();
        SwiftBootTest swiftBootTest = testClass.getAnnotation(SwiftBootTest.class);
        if (swiftBootTest != null) {
            return this.processSwiftBootTestContext(mcc, mergedConfig);
        }
        return mcc;
    }

    private MergedContextConfiguration processSwiftBootTestContext(MergedContextConfiguration mcc,
                                                                   MergedContextConfiguration mergedConfig) {
        Class<?> testClass = mergedConfig.getTestClass();
        CollectContext collectContext = this.buildCollectContext(testClass);
        Set<Class<?>> classes = this.removeUnnecessaryClass(ClassCollectService.collect(collectContext));
        this.modifyFieldAnnotation(classes);
        TestClassContainer.saveClasses(classes);
        TestClassContainer.saveTestClass(testClass);
        BeanUtils.reflectSet(mcc, "classes", classes.toArray(new Class<?>[]{}));
        return mcc;
    }

    private void modifyFieldAnnotation(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            List<Field> fields = BeanUtils.getFieldIncludeSuper(clazz);
            this.setAutoWiredRequiredFalse(fields);
            this.addLazyAnnotation(clazz, fields);
        }
    }

    private void addLazyAnnotation(Class<?> clazz, List<Field> fields) {
        fields.forEach(field -> this.addLazyAnnotation(clazz, field));
    }

    private void addLazyAnnotation(Class<?> clazz, Field field) {
        if (field.getAnnotation(Lazy.class) != null) {
            return;
        }
        if (field.getAnnotation(Autowired.class) == null) {
            return;
        }
        JavasistUtils.addAnnotation2Field(clazz, field.getName(), Lazy.class, null);
    }

    private void setAutoWiredRequiredFalse(List<Field> fields) {
        fields.forEach(this::setAutoWiredRequiredFalse);
    }

    private Set<Class<?>> removeUnnecessaryClass(Set<Class<?>> classes) {
        Set<Class<?>> springframeworkClasses = this.getSpringframeworkClasses(classes);
        Set<Class<?>> abstractClass = this.getAbstractClasses(classes);
        Set<Class<?>> anonymousClasses = this.getAnonymousClasses(classes);
        classes.removeAll(springframeworkClasses);
        classes.removeAll(abstractClass);
        classes.removeAll(anonymousClasses);
        return classes;
    }

    private Set<Class<?>> getAnonymousClasses(Set<Class<?>> classes) {
        return classes.stream()
                .filter(Class::isAnonymousClass)
                .collect(Collectors.toSet());
    }

    private CollectContext buildCollectContext(Class<?> testClass) {
        SwiftBootTest swiftBootTest = testClass.getAnnotation(SwiftBootTest.class);
        Class<?> targetTestClass = this.getTargetTestClass(swiftBootTest);
        CollectContext collectContext = new CollectContext(new HashSet<>());
        collectContext.setTargetClass(targetTestClass);
        collectContext.setTestClass(testClass);
        collectContext.setSwiftBootTest(swiftBootTest);
        return collectContext;
    }

    private Set<Class<?>> getAbstractClasses(Set<Class<?>> classes) {
        return classes.stream()
                .filter(clz -> Modifier.isAbstract(clz.getModifiers()))
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getSpringframeworkClasses(Set<Class<?>> classes) {
        return classes.stream()
                .filter(clz -> clz.getName().startsWith("org.springframework"))
                .collect(Collectors.toSet());
    }

    private Class<?> getTargetTestClass(SwiftBootTest swiftBootTest) {
        Class<?> targetTestClass = swiftBootTest.targetClass();
        if (targetTestClass.isInterface()) {
            throw new SwiftBootTestException("单元测试目标类不能是interface:" + targetTestClass.getSimpleName());
        }
        return targetTestClass;
    }

    @SneakyThrows
    private void setAutoWiredRequiredFalse(Field field) {
        Autowired autowired = field.getAnnotation(Autowired.class);
        if (autowired != null) {
            InvocationHandler h = Proxy.getInvocationHandler(autowired);
            Field hField = h.getClass().getDeclaredField("memberValues");
            hField.setAccessible(true);
            Map memberValues = (Map) hField.get(h);
            memberValues.put("required", false);
        }
    }

}
