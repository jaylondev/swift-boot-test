package io.github.jaylondev.swift.boot.test.config;

import java.lang.reflect.Method;

/**
 * @author jaylon 2023/11/15 16:52
 */
public class SwiftBootTestConfig {

    public static final String sqlFileLocation = "/sql/sample_orders_create_ddl.sql";
    public static final String mapperXmlLocation = "classpath:mapper/*.xml";

    private static volatile SwiftBootTestConfig instance;

    private SwiftBootTestConfig() {}

    // 公共的获取实例方法
    public static SwiftBootTestConfig getInstance() {
        if (instance == null) {
            synchronized (SwiftBootTestConfig.class) {
                if (instance == null) {
                    instance = new SwiftBootTestConfig();
                }
            }
        }
        return instance;
    }

    public static void init(Class<?> testClass, Method testMethod) {

    }
}
