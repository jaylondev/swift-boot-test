package io.github.jaylondev.swift.boot.test.collect.handler;

import io.github.jaylondev.swift.boot.test.collect.CollectContext;
import io.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import io.github.jaylondev.swift.boot.test.collect.TargetFilesClasses;
import io.github.jaylondev.swift.boot.test.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 遍历收集到的单元测试中可能需要使用的类，进一步收集在这些类中通过@AutoWired注解引用的类
 * 递归收集@AutoWired引用链上所有的类
 * 如果某个类为interface接口，将一并收集该接口所有的实现类
 * @author jaylon 2023/9/12 22:41
 */
@Slf4j
public class ReferenceChainClassCollectHandler implements ICollectHandler {
    /**
     * 已经被添加到类收集容器中的类
     */
    private final List<Class<?>> processedClasses = new ArrayList<>();

    @Override
    public void collect(CollectContext collectContext) {
        // 类收集容器
        Set<Class<?>> injectClassList = collectContext.getInjectClassList();
        // 单元测试类
        Class<?> testClass = collectContext.getTestClass();
        // 找到项目编译后/target目录下所有的class，便于收集某个interface的所有实现类
        TargetFilesClasses.getInstance().initTargetAllClasses(testClass);
        // 遍历类收集器中所有的类，递归收集类中所有通过@AutoWired引用的类
        injectClassList.forEach(this::collectionClass);
        // 将收集到的class放入类收集容器中
        injectClassList.addAll(processedClasses);
    }

    public void collectionClass(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            // 收集接口或抽象类
            this.collectionInterfaceClass(clazz);
        } else {
            // 收集实现类
            this.collectionImplClass(clazz);
        }
    }

    private void collectionImplClass(Class<?> clazz) {
        if (processedClasses.contains(clazz)) {
            return;
        }
        processedClasses.add(clazz);
        List<Field> fields = BeanUtils.getFieldIncludeSuper(clazz);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            // 收集类中所有的引用字段
            this.collectionFieldClass(field, fieldType);
        }
    }

    private void collectionFieldClass(Field field, Class<?> clazz) {
        if (field.getAnnotation(Autowired.class) == null) {
            return;
        }
        if (processedClasses.contains(clazz)) {
            return;
        }
        // 如果引用字段类型为List<T>,则尝试收集List集合中的泛型类
        if (clazz.equals(List.class)) {
            try {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Type actualTypeArgument = pt.getActualTypeArguments()[0];
                    // 支持list中对象带泛型List<A<B>>
                    if (actualTypeArgument instanceof ParameterizedType) {
                        ParameterizedType innerPt = (ParameterizedType) actualTypeArgument;
                        clazz = (Class<?>) innerPt.getRawType();
                    } else {
                        clazz = (Class<?>) actualTypeArgument;
                    }
                    this.collectionClass(clazz);
                }
            } catch (Exception e) {
                log.warn("[SwiftBootTest]-[List<> class generic type analysis error field:{}]", field);
            }
            return;
        }
        this.collectionClass(clazz);
    }

    public void collectionInterfaceClass(Class<?> clazz) {
        // 获取接口类的所有实现类
        List<Class<?>> interfaceImplClasses = TargetFilesClasses.getInstance().getInterfaceImplClasses(clazz);
        // 收集实现类
        interfaceImplClasses.forEach(this::collectionImplClass);
    }

    @Override
    public int getOrder() {
        return 15;
    }
}
