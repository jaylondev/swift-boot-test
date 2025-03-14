package io.github.jaylondev.swift.boot.test.collect;

import io.github.jaylondev.swift.boot.test.annotations.ModuleInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
public class TargetFilesClasses {

    private final List<Class<?>> allTargetClasses = new ArrayList<>();

    private static TargetFilesClasses instance;

    private TargetFilesClasses() {}

    public static TargetFilesClasses getInstance() {
        if (instance == null) {
            instance = new TargetFilesClasses();
        }
        return instance;
    }

    /**
     * 找到/tartget目录下所有的class，便于收集某个interface的所有实现类
     */
    public void initTargetAllClasses(Class<?> testClass) {

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
                                    .replace(File.separator, ".");
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
        if (!StringUtils.isEmpty(testModule) && relateModules != null) {
            for (String relateModule : relateModules) {
                modules.add(testClassPath.replaceFirst(testModule, relateModule));
            }
        }
        return modules.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public Class<?> findImpl(Class<?> clazz) {
        for (Class<?> c : allTargetClasses) {
            if (clazz.isAssignableFrom(c) && !c.isInterface()) {
                return c;
            }
        }
        return clazz;
    }

    /**
     * 获取接口所有的实现类
     */
    public List<Class<?>> getInterfaceImplClasses(Class<?> interfaceClass) {
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

}
