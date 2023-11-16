package io.github.jaylondev.swift.boot.test.annotations.listener;

import com.alibaba.druid.pool.DruidDataSource;
import io.github.jaylondev.swift.boot.test.annotations.DbTest;
import io.github.jaylondev.swift.boot.test.annotations.DbTestEnvironment;
import io.github.jaylondev.swift.boot.test.config.MyBatisConfiguration;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

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
        // datasource
        DruidDataSource dataSource = this.buildDruidDataSource(sqlFileLocation);
        // sqlSessionFactory
        SqlSessionFactory sessionFactory = this.buildSqlSessionFactoryBean(mapperXmlLocation, dataSource);
        // MyBatisConfiguration
        Class<MyBatisConfiguration> myBatisConfigurationClass = this.buildMyBatisConfigurationClass(mapperScanBasePackages);
        // 注册数据库连接相关bean到容器
        GenericApplicationContext genericContext = new GenericApplicationContext();
        DefaultListableBeanFactory defaultListableBeanFactory = genericContext.getDefaultListableBeanFactory();
        defaultListableBeanFactory.registerSingleton("dataSource", dataSource);
        defaultListableBeanFactory.registerSingleton("sqlSessionFactory", sessionFactory);
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        defaultListableBeanFactory.registerSingleton("transactionManager", transactionManager);
        defaultListableBeanFactory.registerSingleton("transactionTemplate", new TransactionTemplate(transactionManager));
        genericContext.registerBean(myBatisConfigurationClass);
        // 将新的 ApplicationContext 合并到测试上下文的父上下文中
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext.getApplicationContext();
        applicationContext.setParent(genericContext);
        // 刷新容器，使注册的配置类生效
        applicationContext.refresh();
    }

    @SuppressWarnings("all")
    private Class<MyBatisConfiguration> buildMyBatisConfigurationClass(String[] mapperScanBasePackages) throws NoSuchFieldException, IllegalAccessException {
        Class<MyBatisConfiguration> myBatisConfigurationClass = MyBatisConfiguration.class;
        MapperScan mapperScan = myBatisConfigurationClass.getAnnotation(MapperScan.class);
        InvocationHandler h = Proxy.getInvocationHandler(mapperScan);
        Field hField = h.getClass().getDeclaredField("memberValues");
        hField.setAccessible(true);
        Map memberValues = (Map) hField.get(h);
        memberValues.put("basePackages", mapperScanBasePackages);
        return myBatisConfigurationClass;
    }

    private SqlSessionFactory buildSqlSessionFactoryBean(String mapperXmlLocation, DruidDataSource dataSource) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(resolver.getResources(mapperXmlLocation));
        Configuration configuration = new Configuration();
        configuration.setLogImpl(Slf4jImpl.class);
        sessionFactoryBean.setConfiguration(configuration);
        return sessionFactoryBean.getObject();
    }

    private DruidDataSource buildDruidDataSource(String sqlFileLocation) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:~/mydb;MODE=MySQL;INIT=RUNSCRIPT FROM 'classpath:" + sqlFileLocation + "'");
        dataSource.setUsername("root");
        dataSource.setPassword("test");
        dataSource.setTestWhileIdle(false);
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

    private boolean isEnableDbTest(TestContext testContext) {
        return testContext.getTestMethod().isAnnotationPresent(DbTest.class);
    }
}
