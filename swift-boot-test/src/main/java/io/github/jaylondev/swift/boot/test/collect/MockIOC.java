package io.github.jaylondev.swift.boot.test.collect;

import io.github.jaylondev.swift.boot.test.utils.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public class MockIOC {

    private final static Map<Class<?>, Object> IOC_MAP = new HashMap<>();

    public void add(Class<?> clazz, Object bean) {
        IOC_MAP.put(clazz, bean);
    }

    @SuppressWarnings("all")
    public <T> T get(Class<T> clazz) {
        if (BeanUtils.isInterface(clazz)) {
            clazz = (Class<T>) TargetFilesClasses.getInstance().findImpl(clazz);
        }
        return (T) IOC_MAP.get(clazz);
    }
}
