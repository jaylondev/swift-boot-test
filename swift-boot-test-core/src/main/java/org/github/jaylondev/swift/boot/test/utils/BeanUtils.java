package org.github.jaylondev.swift.boot.test.utils;

import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author jaylon 2023/9/12 22:49
 */
public class BeanUtils {

    @SneakyThrows
    public static void reflectSet(Object object, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(object.getClass(), fieldName);
        ReflectionUtils.makeAccessible(Objects.requireNonNull(field));
        field.set(object, value);
    }

    public static List<Field> getFieldIncludeSuper(Class<?> clz) {
        Field[] declaredFields = clz.getDeclaredFields();
        List<Field> list = new ArrayList<>(Arrays.asList(declaredFields));
        Class<?> superclass = clz.getSuperclass();
        if (superclass != Object.class) {
            List<Field> fields = getFieldIncludeSuper(superclass);
            list.addAll(fields);
        }
        return list;
    }

    public static void fillDefaultValues(Object model) {
        try {
            for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(model)) {
                Class<?> clazz = pd.getPropertyType();
                Method rm = pd.getReadMethod();
                if (rm.invoke(model) == null) {
                    Method wm = pd.getWriteMethod();
                    if (clazz.equals(String.class)) {
                        wm.invoke(model, "");
                    } else if (clazz.equals(Integer.class)) {
                        wm.invoke(model, 0);
                    } else if (clazz.equals(BigDecimal.class)) {
                        wm.invoke(model, BigDecimal.ZERO);
                    } else if (clazz.equals(Long.class)) {
                        wm.invoke(model, 0L);
                    } else if (clazz.equals(Short.class)) {
                        wm.invoke(model, (short) 0);
                    } else if (clazz.equals(Double.class)) {
                        wm.invoke(model, (double) 0);
                    } else if (clazz.equals(Date.class)) {
                        wm.invoke(model, new Date());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("字段默认值设置出现异常", e);
        }
    }
}
