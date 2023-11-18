package io.github.jaylondev.swift.boot.test.config;

import io.github.jaylondev.swift.boot.test.annotations.DbTest;
import io.github.jaylondev.swift.boot.test.annotations.DbTestEnvironment;
import io.github.jaylondev.swift.boot.test.exception.SwiftBootTestException;
import lombok.Builder;
import lombok.Data;
import org.springframework.test.context.TestContext;
import org.springframework.util.StringUtils;

/**
 * @author jaylon 2023/11/17 22:39
 */
@Data
@Builder
public class DbTestEnvironmentConfig {
    /**
     * 测试类class对象
     */
    private Class<?> testClass;
    /**
     * 初始化SQL文件地址
     */
    private String sqlFilePath;
    /**
     * mybatis mapper类包路径
     */
    private String[] mapperScanBasePackages;
    /**
     * mapper.xml文件路径
     */
    private String mapperXmlLocation;
    /**
     * h2数据库连接url模板
     */
    private static final String JDBC_URL_TEMPLATE = "jdbc:h2:~/mydb;MODE=MySQL;INIT=RUNSCRIPT FROM 'classpath:%s'";
    public static final String DATABASE_USER = "root";
    public static final String DATABASE_PASSWORD = "test";
    public static final String DATABASE_DRIVER = "org.h2.Driver";

    public static final String BEAN_NAME_DATASOURCE = "dataSource";
    public static final String BEAN_NAME_TRANSACTIONMANAGER = "transactionManager";
    public static final String BEAN_NAME_SQLSESSIONFACTORY = "sqlSessionFactory";
    public static final String BEAN_NAME_TRANSACTIONTEMPLATE = "transactionTemplate";

    public static DbTestEnvironmentConfig checkAndBuildDbEnvironmentConfig(TestContext testContext) {
        DbTest dbTest = testContext.getTestMethod().getAnnotation(DbTest.class);
        if (dbTest == null) {
            throw new SwiftBootTestException("进行数据库测试需要在测试方法上标记@DbTest注解");
        }
        String sqlFilePath = dbTest.sqlFile();
        if (StringUtils.isEmpty(sqlFilePath)) {
            throw new SwiftBootTestException("请在@DbTest注解中配置数据库表DDL文件地址，如:/db/create_table.sql");
        }
        // mapper文件地址，mapper.xml地址
        Class<?> testClass = testContext.getTestClass();
        DbTestEnvironment dbTestEnvironment = testClass.getAnnotation(DbTestEnvironment.class);
        if (dbTestEnvironment == null) {
            throw new SwiftBootTestException("请在测试类上标记@DbTestEnvironment注解");
        }
        String[] mapperScanBasePackages = dbTestEnvironment.mapperBasePackages();
        String mapperXmlLocation = dbTestEnvironment.mapperXmlLocation();
        if (mapperScanBasePackages.length < 1 || StringUtils.isEmpty(mapperXmlLocation)) {
            throw new SwiftBootTestException("请在@DbTestEnvironment注解中配置mapper类的包路径及mapper.xml文件路径");
        }
        return DbTestEnvironmentConfig.builder()
                .mapperScanBasePackages(mapperScanBasePackages)
                .mapperXmlLocation(mapperXmlLocation)
                .sqlFilePath(sqlFilePath)
                .testClass(testClass)
                .build();
    }

    public String buildJdbcUrl() {
        return String.format(JDBC_URL_TEMPLATE, this.sqlFilePath);
    }
}
