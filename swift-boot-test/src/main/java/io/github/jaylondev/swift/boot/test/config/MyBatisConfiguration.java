package io.github.jaylondev.swift.boot.test.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author jaylon 2023/11/15 16:52
 */
@Configuration
@MapperScan(basePackages = "")
public class MyBatisConfiguration {
}
