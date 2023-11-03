package org.github.jaylondev.swift.boot.test.collect.handler;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.annotations.ModuleInfo;
import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import org.github.jaylondev.swift.boot.test.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 遍历收集到的单元测试中可能需要使用的类，进一步收集在这些类中通过@AutoWired注解引用的类
 * 递归收集@AutoWired引用链上所有的类
 * 如果某个类为interface接口，将一并收集该接口所有的实现类
 * @author jaylon 2023/9/12 22:41
 */
@Slf4j
public class ReferenceChainClassCollectHandler implements ICollectHandler {
    /**
     * /target目录下所有的class集合
     */
    private final List<Class<?>> allTargetClasses = new ArrayList<>();
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
        this.initTargetAllClasses(testClass);
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
                    clazz = (Class<?>) pt.getActualTypeArguments()[0];
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
        List<Class<?>> interfaceImplClasses = this.getInterfaceImplClasses(clazz);
        // 收集实现类
        interfaceImplClasses.forEach(this::collectionImplClass);
    }

    /**
     * 获取接口所有的实现类
     */
    private List<Class<?>> getInterfaceImplClasses(Class<?> interfaceClass) {
        if (interfaceClass == Logger.class) {
            return Collections.emptyList();
        }
        List<Class<?>> allSubclass = new ArrayList<>();
        for (Class<?> c : allTargetClasses) {
            if (interfaceClass.isAssignableFrom(c) && !c.isInterface()) {
                allSubclass.add(c);
            }
        }
        return allSubclass;
    }

    /**
     * 找到/tartget目录下所有的class，便于收集某个interface的所有实现类
     */
    private void initTargetAllClasses(Class<?> testClass) {

        List<String> modules = this.getModuleNameList(testClass);
        try {
            for (String classPathItem : modules) {
                File tartetPath = new File(classPathItem).getParentFile();
                File classPathFile = new File(tartetPath, "classes");
                if (!classPathFile.exists()) {
                    continue;
                }
                String classPathStr = classPathFile.toString();
                Files.walkFileTree(classPathFile.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (file.getFileName().toString().endsWith(".class")) {
                            String str = file.toString()
                                    .replace(classPathStr, "")
                                    .replace(".class", "")
                                    .replace("\\", ".");
                            try {
                                Class<?> clz = Class.forName(str.substring(1));
                                allTargetClasses.add(clz);
                            } catch (Exception e) {
                                log.warn("[SwiftBootTest] load class {} fail exception:", str.substring(1), e);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (Exception e) {
            log.warn("[SwiftBootTest] modules read error exception:", e);
        }
    }

    /**
     * 获取所有的module全路径名
     */
    private List<String> getModuleNameList(Class<?> testClass) {
        ModuleInfo moduleInfo = testClass.getAnnotation(ModuleInfo.class);
        String testModule = null;
        String[] relateModules = null;
        if (moduleInfo != null) {
            // 单元测试类所在的module名
            testModule = moduleInfo.testModule();
            // 关联的其他module
            relateModules = moduleInfo.relateModules();
        }
        ClassLoader classLoader = testClass.getClassLoader();
        //单元测试类class所在的包全路径
        String testClassPath = Objects.requireNonNull(classLoader.getResource("")).getFile();
        List<String> modules = new ArrayList<>();
        modules.add(testClassPath);
        if (!StringUtils.isEmpty(testModule) && relateModules != null && relateModules.length > 0) {
            for (String relateModule : relateModules) {
                modules.add(testClassPath.replace(testModule, relateModule));
            }
        }
        return modules.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public int getOrder() {
        return 15;
    }
}
