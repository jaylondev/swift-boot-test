package io.github.jaylondev.swift.boot.test.factory;

import com.alibaba.druid.pool.DruidDataSource;
import io.github.jaylondev.swift.boot.test.config.DbTestEnvironmentConfig;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author jaylon 2023/11/17 22:55
 */
public class DbTestEnvironmentFactory {

    private DbTestEnvironmentConfig config;

    private static DbTestEnvironmentFactory instance;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    private SqlSessionFactory sqlSessionFactory;

    private TransactionTemplate transactionTemplate;

    private DbTestEnvironmentFactory() {};

    public static DbTestEnvironmentFactory getInstance(DbTestEnvironmentConfig dbConfig) {
        if (instance == null) {
            instance = new DbTestEnvironmentFactory();
            instance.config = dbConfig;
        }
        return instance;
    }

    public DataSource createDataSource() {
        if (dataSource == null) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(config.buildJdbcUrl());
            dataSource.setUsername(DbTestEnvironmentConfig.DATABASE_USER);
            dataSource.setPassword(DbTestEnvironmentConfig.DATABASE_PASSWORD);
            dataSource.setTestWhileIdle(false);
            dataSource.setDriverClassName(DbTestEnvironmentConfig.DATABASE_DRIVER);
            this.dataSource = dataSource;
        }
        return dataSource;
    }

    public PlatformTransactionManager createTransactionManager() {
        if (transactionManager == null) {
            transactionManager = new DataSourceTransactionManager(this.createDataSource());
        }
        return transactionManager;
    }

    public SqlSessionFactory createSqlSessionFactory() throws Exception {
        if (sqlSessionFactory == null) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(this.createDataSource());
            sessionFactoryBean.setMapperLocations(resolver.getResources(config.getMapperXmlLocation()));
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.setLogImpl(Slf4jImpl.class);
            sessionFactoryBean.setConfiguration(configuration);
            sqlSessionFactory = sessionFactoryBean.getObject();
        }
        return sqlSessionFactory;
    }

    public TransactionTemplate createTransactionTemplate( ) {
        if (transactionTemplate == null) {
            transactionTemplate = new TransactionTemplate(this.createTransactionManager());
        }
        return transactionTemplate;
    }
}
