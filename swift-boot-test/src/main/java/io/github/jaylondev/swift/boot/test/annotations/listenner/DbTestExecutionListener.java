package io.github.jaylondev.swift.boot.test.annotations.listenner;

import com.alibaba.druid.pool.DruidDataSource;
import io.github.jaylondev.swift.boot.test.annotations.DbTest;
import io.github.jaylondev.swift.boot.test.annotations.DbTestEnvironment;
import io.github.jaylondev.swift.boot.test.config.SwiftBootTestConfig;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author jaylon 2023/11/15 17:05
 */
public class DbTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (this.isEnableDbTest(testContext)) {
            this.enableDbEnvironment(testContext);
        }
    }

    private void enableDbEnvironment(TestContext testContext) throws Exception {
        DbTest dbTest = testContext.getTestMethod().getAnnotation(DbTest.class);
        String sqlFileLocation = dbTest.sqlFile();
        // mapper文件地址，mapper.xml地址
        Class<?> testClass = testContext.getTestClass();
        DbTestEnvironment dbTestEnvironment = testClass.getAnnotation(DbTestEnvironment.class);
        String[] mapperScanBasePackages = dbTestEnvironment.mapperScanBasePackages();
        String mapperXmlLocation = dbTestEnvironment.mapperXmlLocation();

        // 注册数据库相关类到容器中
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext.getApplicationContext();
        DataSource dataSource = dataSource();
        PlatformTransactionManager transactionManager = transactionManager(dataSource);
        SqlSessionFactory sqlSessionFactory = sqlSessionFactory(dataSource);
        applicationContext.getBeanFactory().registerSingleton("dataSource", dataSource);
        applicationContext.getBeanFactory().registerSingleton("transactionManager", transactionManager);
        applicationContext.getBeanFactory().registerSingleton("sqlSessionFactory", sqlSessionFactory);
        applicationContext.getBeanFactory().registerSingleton("transactionTemplate", transactionTemplate(transactionManager));



        // 扫描符合条件的类
        List<Class<?>> mapperClasses = this.scanPackages(mapperScanBasePackages, testContext.getTestClass());

        // 创建 Mapper 接口的代理对象并注册到容器中
        for (Class<?> mapperClass : mapperClasses) {
            MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<>(mapperClass);
            // 设置其他属性，如 SqlSessionFactory
            mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
            // 注册 Mapper 接口代理对象到容器中
            applicationContext.getBeanFactory().registerSingleton(mapperClass.getSimpleName(), mapperFactoryBean.getObject());
        }
    }

    private List<Class<?>> scanPackages(String[] mapperScanBasePackages, Class<?> testClass) {
        List<Class<?>> classes = new ArrayList<>();

        for (String basePackage : mapperScanBasePackages) {
            String packagePath = basePackage.replace('.', '/');
            Enumeration<URL> resources;
            try {
                resources = testClass.getClassLoader().getResources(packagePath);
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

    private static List<Class<?>> findClasses(File directory, String packageName) {
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

    private boolean isEnableDbTest(TestContext testContext) {
        return testContext.getTestMethod().isAnnotationPresent(DbTest.class);
    }

    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:~/mydb;MODE=MySQL;INIT=RUNSCRIPT FROM 'classpath:" + SwiftBootTestConfig.sqlFileLocation + "'");
        dataSource.setUsername("root");
        dataSource.setPassword("test");
        dataSource.setTestWhileIdle(false);
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(resolver.getResources(SwiftBootTestConfig.mapperXmlLocation));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLogImpl(Slf4jImpl.class);
        sessionFactoryBean.setConfiguration(configuration);
        return sessionFactoryBean.getObject();
    }

    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
