package io.github.jaylondev.swift.boot.test.collect.handler;

import com.alibaba.druid.pool.DruidDataSource;
import io.github.jaylondev.swift.boot.test.annotations.DbTest;
import io.github.jaylondev.swift.boot.test.annotations.DbTestEnvironment;
import io.github.jaylondev.swift.boot.test.collect.CollectContext;
import io.github.jaylondev.swift.boot.test.collect.ICollectHandler;
import io.github.jaylondev.swift.boot.test.config.DbTestEnvironmentConfig;
import io.github.jaylondev.swift.boot.test.config.MyBatisConfiguration;
import io.github.jaylondev.swift.boot.test.postprocessor.BeanDefinitionLazyInitModifyPostProcessor;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 配置类收集器
 * @author jaylon 2023/8/7 22:41
 */
public class ConfigClassCollectHandler implements ICollectHandler {

    @Override
    public void collect(CollectContext collectContext) {
        Set<Class<?>> classListContailer = collectContext.getInjectClassList();
        classListContailer.add(BeanDefinitionLazyInitModifyPostProcessor.class);
        if (!Objects.equals(Void.class, collectContext.getTargetClass())) {
            classListContailer.add(collectContext.getTargetClass());
        }


        try {
            extracted(collectContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void extracted(CollectContext collectContext) throws Exception {
        String sqlFileLocation = "/db/sample_orders_create_ddl.sql";
        // mapper文件地址，mapper.xml地址
        Class<?> testClass = collectContext.getTestClass();
        DbTestEnvironment dbTestEnvironment = testClass.getAnnotation(DbTestEnvironment.class);
        String[] mapperScanBasePackages = dbTestEnvironment.mapperScanBasePackages();
        String mapperXmlLocation = dbTestEnvironment.mapperXmlLocation();
//        collectContext.getInjectClassList().add(DbTestEnvironmentConfig.class);



        /*Class<MyBatisConfiguration> myBatisConfigurationClass = this.buildMyBatisConfigurationClass(mapperScanBasePackages);
        collectContext.getInjectClassList().add(myBatisConfigurationClass);*/

//        collectContext.getInjectClassList().add(MyBatisConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private Class<MyBatisConfiguration> buildMyBatisConfigurationClass(String[] mapperScanBasePackages) throws Exception {
        Class<MyBatisConfiguration> myBatisConfigurationClass = MyBatisConfiguration.class;
        MapperScan mapperScan = myBatisConfigurationClass.getAnnotation(MapperScan.class);
        InvocationHandler h = Proxy.getInvocationHandler(mapperScan);
        Field hField = h.getClass().getDeclaredField("memberValues");
        hField.setAccessible(true);
        Map memberValues = (Map) hField.get(h);
        memberValues.put("basePackages", mapperScanBasePackages);
        return myBatisConfigurationClass;
    }

}
