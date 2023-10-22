package org.github.jaylondev.swift.boot.test.sample.dao.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.sample.dao.mapper.SampleMapperA;
import org.springframework.stereotype.Component;

/**
 * @author jaylon 2023/10/6 16:04
 */
@Slf4j
@Component
public class SampleMapperVirtualImpl implements SampleMapperA {
    @Override
    public int update() {
        log.info("----------数据库改动执行完成-------");
        return 1;
    }
}
