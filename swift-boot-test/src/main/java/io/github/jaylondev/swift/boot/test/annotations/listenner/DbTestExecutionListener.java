package io.github.jaylondev.swift.boot.test.annotations.listenner;

import io.github.jaylondev.swift.boot.test.annotations.DbTest;
import io.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;
import io.github.jaylondev.swift.boot.test.config.DbTestEnvironmentConfig;
import io.github.jaylondev.swift.boot.test.factory.DbTestEnvironmentFactory;
import io.github.jaylondev.swift.boot.test.utils.MapperUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        DbTestEnvironmentConfig dbconfig = DbTestEnvironmentConfig.checkAndBuildDbEnvironmentConfig(testContext);
        DbTestEnvironmentFactory dbFactory = DbTestEnvironmentFactory.getInstance(dbconfig);
        this.registerDbBean(testContext, dbFactory);
        this.registerMapperProxy(testContext, dbconfig, dbFactory);
    }

    private void registerMapperProxy(TestContext testContext, DbTestEnvironmentConfig dbconfig, DbTestEnvironmentFactory dbFactory) throws Exception {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext.getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        ClassLoader classLoader = this.getClassLoader(testContext);
        String[] basePackages = dbconfig.getMapperScanBasePackages();
        SqlSessionFactory sqlSessionFactory = dbFactory.createSqlSessionFactory();
        List<Object> mapperProxys = MapperUtils.createMapperProxy(basePackages, classLoader, sqlSessionFactory);
        // 注册 Mapper 接口代理对象到容器中
        mapperProxys.forEach(mapperProxy -> beanFactory.registerSingleton(mapperProxy.getClass().getSimpleName(), mapperProxy));
    }

    private ClassLoader getClassLoader(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        return Optional.of(testClass)
                .map(input -> input.getAnnotation(SwiftBootTest.class))
                .map(SwiftBootTest::targetClass)
                .filter(input -> !Objects.equals(Void.class, input))
                .map(Class::getClassLoader)
                .orElse(testClass.getClassLoader());
    }

    private void registerDbBean(TestContext testContext, DbTestEnvironmentFactory dbFactory) throws Exception {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext.getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        DataSource dataSource = dbFactory.createDataSource();
        PlatformTransactionManager transactionManager = dbFactory.createTransactionManager();
        SqlSessionFactory sqlSessionFactory = dbFactory.createSqlSessionFactory();
        TransactionTemplate transactionTemplate = dbFactory.createTransactionTemplate();
        beanFactory.registerSingleton(DbTestEnvironmentConfig.BEAN_NAME_DATASOURCE, dataSource);
        beanFactory.registerSingleton(DbTestEnvironmentConfig.BEAN_NAME_TRANSACTIONMANAGER, transactionManager);
        beanFactory.registerSingleton(DbTestEnvironmentConfig.BEAN_NAME_SQLSESSIONFACTORY, sqlSessionFactory);
        beanFactory.registerSingleton(DbTestEnvironmentConfig.BEAN_NAME_TRANSACTIONTEMPLATE, transactionTemplate);
    }

    private boolean isEnableDbTest(TestContext testContext) {
        return testContext.getTestMethod().isAnnotationPresent(DbTest.class);
    }

}
