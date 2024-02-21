package io.github.jaylondev.swift.boot.test.sample.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jaylon 2023/10/6 15:07
 */
@SpringBootApplication
@MapperScan("io.github.jaylondev.swift.boot.test.sample.api.dal.mapper")
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
