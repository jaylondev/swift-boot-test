package org.github.jaylondev.swift.boot.test.collect.handler;

import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.collect.CollectContext;
import org.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import org.github.jaylondev.swift.boot.test.config.Configurations;
import org.github.jaylondev.swift.boot.test.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
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
 * @author jaylon 2023/9/12 22:41
 */
@Slf4j
public class ReferenceChainClassCollectHandler implements ICollectHandler {

    private final List<Class<?>> allTargetClasses = new ArrayList<>();
    private final List<Class<?>> processedClasses = new ArrayList<>();

    @Override
    public void collect(CollectContext collectContext) {
        Set<Class<?>> injectClassList = collectContext.getInjectClassList();
        Class<?> testClass = collectContext.getTestClass();
        this.initTargetAllClasses(testClass.getClassLoader());
        injectClassList.forEach(this::collectionClass);
        injectClassList.addAll(processedClasses);
    }

    public void collectionClass(Class<?> clazz) {
        if (clazz.isInterface()) {
            this.collectionInterfaceClass(clazz);
        } else {
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
        if (clazz.equals(List.class)) {
            try {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    clazz = (Class<?>) pt.getActualTypeArguments()[0];
                    this.collectionClass(clazz);
                }
            } catch (Exception e) {
                Log.warn("[SwiftBootTest]-[List<> class generic type analysis error field:{}]", field);
            }
            return;
        }
        this.collectionClass(clazz);
    }

    public void collectionInterfaceClass(Class<?> clazz) {
        List<Class<?>> interfaceImplClasses = this.getInterfaceImplClasses(clazz);
        interfaceImplClasses.forEach(this::collectionImplClass);
    }

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

    private void initTargetAllClasses(ClassLoader classLoader) {
       List<String> modules = this.getModuleNameList(classLoader);
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

    private List<String> getModuleNameList(ClassLoader classLoader) {
        String currentModule = Configurations.getInstance().getModuleName();
        String testClassPath = Objects.requireNonNull(classLoader.getResource("")).getFile();
        List<String> modules = new ArrayList<>();
        modules.add(testClassPath);
        String relateModules = Configurations.getInstance().getDependencyModules();
        if (!StringUtils.isEmpty(relateModules)) {
            String[] relateModuleArray = relateModules.split(",");
            if (relateModuleArray.length > 0) {
                for (String relateModule : relateModuleArray) {
                    modules.add(testClassPath.replace(currentModule, relateModule));
                }
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
