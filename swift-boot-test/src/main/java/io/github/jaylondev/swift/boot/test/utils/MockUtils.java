package io.github.jaylondev.swift.boot.test.utils;

import io.github.jaylondev.swift.boot.test.exception.SwiftBootTestException;
import lombok.extern.slf4j.Slf4j;
import org.mockito.mock.MockCreationSettings;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author jaylon 2023/9/15 7:50
 */
@Slf4j
public class MockUtils {

    public static final String CLASS_NAME_MOCKUTIL = "org.mockito.internal.util.MockUtil";

    private static Class<?> mockitoMockUtilClazz;

    private static Object mockitoMockUtilInstace;

    static {
        try {
            mockitoMockUtilClazz = Class.forName(CLASS_NAME_MOCKUTIL);
        } catch (ClassNotFoundException e) {
            log.warn("[SwiftBootTest] - class {} is not found", CLASS_NAME_MOCKUTIL, e);
        }
        Constructor<?> mockitoMockUtilConstructor = null;
        if (mockitoMockUtilClazz != null) {
            try {
                mockitoMockUtilConstructor = mockitoMockUtilClazz.getDeclaredConstructor();
                mockitoMockUtilConstructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                log.info("[SwiftBootTest] - {} maybe a static class", CLASS_NAME_MOCKUTIL);
            }
        }
        if (mockitoMockUtilConstructor != null) {
            try {
                mockitoMockUtilInstace = mockitoMockUtilConstructor.newInstance();
            } catch (Exception e) {
                log.info("[SwiftBootTest] - get instance for {} fail", CLASS_NAME_MOCKUTIL);
            }
        }
    }

    public static void mock(Object target, String fieldName, Object mock) {
        ReflectionTestUtils.setField(target, fieldName, mock);
    }

    public static void mock(Object targetBean, Object mockBean) {
        // mock替身类的真实类型
        String mockTypeName = getTypeName(mockBean);
        if (StringUtils.isEmpty(mockTypeName)) {
            throw new SwiftBootTestException("需要mock的bean类型获取失败，请尝试替换为Mockutils.mock(Object target, String fieldName, Object mock)方法进行mock");
        }
        ReflectionUtils.doWithFields(targetBean.getClass(), field -> {
            String fieldTypeName = field.getType().getTypeName();
            if (Objects.equals(fieldTypeName, mockTypeName)) {
                ReflectionTestUtils.setField(targetBean, field.getName(), mockBean);
            }
        });
    }

    private static String getTypeName(Object mockBean) {
        String mockTypeName = null;
        if (isMockBean(mockBean)) {
            mockTypeName = getTypeNameByMockBean(mockBean);
        } else {
            mockTypeName = mockBean.getClass().getTypeName();
        }
        return mockTypeName;
    }

    @SuppressWarnings("all")
    private static String getTypeNameByMockBean(Object mockBean) {
        try {
            Method method = getMethod("getMockSettings", Object.class);
            MockCreationSettings invoke = (MockCreationSettings) method.invoke(mockitoMockUtilInstace, mockBean);
            return invoke.getTypeToMock().getTypeName();
        } catch (Exception e) {
            log.error("[SwiftBootTest] 获取mock对象class类型时发生异常:", e);
            throw new SwiftBootTestException("mockito版本不兼容，请尝试替换为Mockutils.mock(Object target, String fieldName, Object mock)方法进行mock");
        }
    }

    @SuppressWarnings("all")
    private static boolean isMockBean(Object mockBean) {
        try {
            Method method = getMethod("isMock", Object.class);
            Object invoke = method.invoke(mockitoMockUtilInstace, mockBean);
            if (invoke == null) {
                throw new IllegalStateException();
            }
            return (boolean) invoke;
        } catch (Exception e) {
            log.error("[SwiftBootTest] {}.isMock invoke throw a excption:", CLASS_NAME_MOCKUTIL, e);
            throw new SwiftBootTestException("mockito版本不兼容，请尝试替换为Mockutils.mock(Object target, String fieldName, Object mock)方法进行mock");
        }
    }

    private static Method getMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (mockitoMockUtilClazz == null) {
            return null;
        }
        return mockitoMockUtilClazz.getMethod(methodName, parameterTypes);
    }

}
