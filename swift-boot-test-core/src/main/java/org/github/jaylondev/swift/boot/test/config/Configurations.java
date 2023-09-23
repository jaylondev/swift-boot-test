package org.github.jaylondev.swift.boot.test.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.exception.SwiftBootTestException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jaylon 2023/8/7 22:42
 */
@Getter
@Slf4j
public class Configurations {

    private boolean prepared;

    /**
     * 应用启动类依赖的其他模块，如core、service、dao等，多个以英文,分隔
     */
    private String dependencyModules;
    /**
     * 应用启动类的module模块名
     */
    private String moduleName;

    private static final String TEST_PROPERTIES_FILE_NAME = "swiftBootTest.properties";
    public static final String DEPENDENCY_MODULES = "dependency.modules";
    public static final String MODULE_NAME = "module.name";

    private static Configurations INSTANCE;

    private Configurations() {}

    public static Configurations getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configurations();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    private void init() {
        if (!prepared) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(TEST_PROPERTIES_FILE_NAME);
            if (inputStream == null) {
                log.error("[Swift-Boot-Test] resources文件夹中缺少SwiftBootTest必须的配置文件：swiftBootTest.properties");
                throw new SwiftBootTestException("请检查swiftBootTest.properties配置文件是否存在");
            }
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                this.moduleName = properties.getProperty(MODULE_NAME);
                this.dependencyModules = properties.getProperty(DEPENDENCY_MODULES);
                prepared = true;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
