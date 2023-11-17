package io.github.jaylondev.swift.boot.test.config;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jaylon 2023/11/15 16:52
 */
@Configuration
public class MyBatisConfiguration {
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("io.github.jaylondev.swift.boot.test.sample.api.dal.mapper"); // 指定扫描的包路径
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory"); // 指定SqlSessionFactory
        return configurer;
    }
}
