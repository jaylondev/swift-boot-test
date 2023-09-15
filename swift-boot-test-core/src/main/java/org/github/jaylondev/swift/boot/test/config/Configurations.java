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

    private boolean connectDataBase;
    private String dbHost;
    private Integer dbPort;
    private String database;
    private String dbUserName;
    private String dbPassWord;
    private String mapperLocation;

    private boolean connectRedis;
    private String redisNodes;
    private String redisPassWord;

    private String dependencyModules;
    private String moduleName;

    private static final String TEST_PROPERTIES_FILE_NAME = "swiftBootTest.properties";
    public static final String DB_HOST = "db.host";
    public static final String DB_PORT = "db.port";
    public static final String DB_DATABASE = "db.database";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";
    public static final String MAPPER_LOCATION = "mapper.location";
    public static final String REDIS_CLUSTER_NODES = "redis.cluster.nodes";
    public static final String REDIS_PASSWORD = "redis.password";
    public static final String DEPENDENCY_MODULES = "dependency.modules";
    public static final String MODULE_NAME = "module.name";
    public static final String CONNECT_DATABASE = "connect.database";
    public static final String CONNECT_REDIS = "connect.redis";

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
                this.dbHost = properties.getProperty(DB_HOST);
                this.dbPort = Integer.valueOf(properties.getProperty(DB_PORT));
                this.database = properties.getProperty(DB_DATABASE);
                this.dbPassWord = properties.getProperty(DB_PASSWORD);
                this.dbUserName = properties.getProperty(DB_USERNAME);
                this.connectDataBase = Boolean.parseBoolean(properties.getProperty(CONNECT_DATABASE));
                this.mapperLocation = properties.getProperty(MAPPER_LOCATION);
                this.connectRedis = Boolean.parseBoolean(properties.getProperty(CONNECT_REDIS));
                this.redisNodes = properties.getProperty(REDIS_CLUSTER_NODES);
                this.redisPassWord = properties.getProperty(REDIS_PASSWORD);
                this.moduleName = properties.getProperty(MODULE_NAME);
                this.dependencyModules = properties.getProperty(DEPENDENCY_MODULES);
                prepared = true;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
