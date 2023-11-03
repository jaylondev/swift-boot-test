package org.github.jaylondev.swift.boot.test.constants;

import org.github.jaylondev.swift.boot.test.annotations.IncludeComponent;
import org.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;

import java.util.*;

/**
 * @author jaylon 2023/8/20 21:57
 */
public class TestClassContainer {

    private static final Set<Class<?>> classesList = new HashSet<>();

    private static Class<?> testClass;

    public static void saveTestClass(Class<?> clazz) {
        testClass = clazz;
    }

    public static void saveClasses(Set<Class<?>> list) {
        classesList.addAll(list);
    }

    public static List<Class<?>> getPreparedModifyLazyInitClasses() {
        List<Class<?>> unSetLazyClasses = new ArrayList<>(classesList);
        SwiftBootTest swiftBootTest = testClass.getAnnotation(SwiftBootTest.class);
        if (swiftBootTest != null) {
            Class<?>[] unLazyClasses = swiftBootTest.unLazyClasses();
            Class<?>[] includeComponent = swiftBootTest.includeComponent();
            unSetLazyClasses.removeAll(Arrays.asList(unLazyClasses));
            unSetLazyClasses.removeAll(Arrays.asList(includeComponent));
        }
        IncludeComponent commonIncludeComponent = testClass.getAnnotation(IncludeComponent.class);
        if (commonIncludeComponent != null) {
            Class<?>[] commonIncludeClasses = commonIncludeComponent.value();
            unSetLazyClasses.removeAll(Arrays.asList(commonIncludeClasses));
        }
        return unSetLazyClasses;
    }
}
