package io.github.jaylondev.swift.boot.test.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author jaylon 2023/11/18 15:51
 */
public class ClassUtils {

    public static List<Class<?>> scanPackages(String[] mapperScanBasePackages, ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<>();

        for (String basePackage : mapperScanBasePackages) {
            String packagePath = basePackage.replace('.', '/');
            Enumeration<URL> resources;
            try {
                resources = classLoader.getResources(packagePath);
            } catch (IOException e) {
                throw new RuntimeException("Error scanning classes in package: " + basePackage, e);
            }

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    classes.addAll(findClasses(directory, basePackage));
                }
            }
        }

        return classes;
    }

    public static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Error loading class: " + className, e);
                    }
                }
            }
        }

        return classes;
    }
}
