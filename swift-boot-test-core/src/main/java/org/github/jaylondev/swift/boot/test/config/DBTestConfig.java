package org.github.jaylondev.swift.boot.test.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author jaylon 2023/8/20 21:33
 */
public class DBTestConfig {

    private static final String DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    @Bean
    public DataSource dataSource() {
        Configurations config = Configurations.getInstance();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(buildUrl());
        druidDataSource.setUsername(config.getDbUserName());
        druidDataSource.setPassword(config.getDbPassWord());
        druidDataSource.setDriverClassName(DB_DRIVER_CLASS_NAME);
        return druidDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        Configurations config = Configurations.getInstance();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(config.getMapperLocation()));
        Configuration configuration = new Configuration();
        configuration.setLogImpl(Slf4jImpl.class);
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    private String buildUrl() {
        Configurations config = Configurations.getInstance();
        return "jdbc:mysql://"+
                config.getDbHost() +
                ":" +
                config.getDbPort() +
                "/" +
                config.getDatabase() +
                "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false";
    }
}
