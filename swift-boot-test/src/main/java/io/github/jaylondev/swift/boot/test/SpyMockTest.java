package io.github.jaylondev.swift.boot.test;

import io.github.jaylondev.swift.boot.test.annotations.IgnoreSpy;
import io.github.jaylondev.swift.boot.test.collect.MockIOC;
import io.github.jaylondev.swift.boot.test.collect.TargetFilesClasses;
import io.github.jaylondev.swift.boot.test.utils.BeanUtils;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class SpyMockTest {

    private final List<Class<?>> completeMockClasses = new ArrayList<>();

    private final List<Class<?>> ignoreFields = new ArrayList<>();

    private final List<Class<?>> ignoreClasses = new ArrayList<>();

    private final List<String> ignoreClassesSuffixs = new ArrayList<>();

    private final MockIOC mockIOC = new MockIOC();

    public MockIOC spyMock(Object testObj) throws Exception {
        TargetFilesClasses.getInstance().initTargetAllClasses(getClass());
        this.initIgnores(getClass());
        this.mockReference(testObj, testObj.getClass());
        return mockIOC;
    }

    private void mockReference(Object instance, Class<?> clazz) throws Exception {
        clazz = this.resolveImplementation(clazz);
        if (this.isSkipClass(clazz)) {
            return;
        }
        completeMockClasses.add(clazz);
        List<Field> fields = BeanUtils.getfieldsIncludeSuperNonNull(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldImplClass = this.resolveImplementation(field.getType());
            if (this.isSkipField(instance, field, fieldImplClass)) {
                continue;
            }
            Object spy = this.getFromMockIOC(fieldImplClass);
            field.set(instance, spy);
            mockReference(spy, fieldImplClass);
        }
    }

    private Object getFromMockIOC(Class<?> fieldImplClass) throws Exception {
        Object spyBean = mockIOC.get(fieldImplClass);
        if (spyBean != null) {
            return spyBean;
        }
        Object spy = Mockito.spy(fieldImplClass);
        mockIOC.add(fieldImplClass, spy);
        return spy;
    }

    private boolean isSkipField(Object instance, Field field, Class<?> fieldImplClass) throws IllegalAccessException {
        if (field.get(instance) != null) {
            return true;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return true;
        }
        if (fieldImplClass == null) {
            return true;
        }
        Class<?> fieldClass = field.getType();
        if (fieldClass.isPrimitive() || ignoreFields.contains(fieldClass) || ignoreFields.contains(fieldImplClass)) {
            return true;
        }
        return false;
    }

    private boolean isSkipClass(Class<?> clazz) {
        if (clazz == null) {
            return true;
        }
        if (completeMockClasses.contains(clazz)) {
            return true;
        }
        for (String suffix : ignoreClassesSuffixs) {
            if (clazz.getSimpleName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private Class<?> resolveImplementation(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        if (BeanUtils.isInterface(clazz)) {
            return TargetFilesClasses.getInstance().findImpl(clazz);
        }
        return clazz;
    }

    private void initIgnores(Class<? extends SpyMockTest> testClass) {
        ignoreFields.add(String.class);
        ignoreFields.add(Integer.class);
        ignoreFields.add(Date.class);
        ignoreFields.add(Map.class);
        IgnoreSpy ignoreSpy = testClass.getAnnotation(IgnoreSpy.class);
        if (ignoreSpy == null) {
            return;
        }
        ignoreFields.addAll(new ArrayList<>(Arrays.asList(ignoreSpy.fields())));
        ignoreClasses.addAll(new ArrayList<>(Arrays.asList(ignoreSpy.classes())));
        ignoreClassesSuffixs.addAll(new ArrayList<>(Arrays.asList(ignoreSpy.classSuffixs())));
    }
}
