package org.github.jaylondev.swift.boot.test.sample.api.dal.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleMapperA;
import org.springframework.stereotype.Component;

/**
 * @author jaylon 2023/11/3 10:11
 */
@Component
@Slf4j
public class SampleMapperAVirtualImpl implements SampleMapperA {

    @Override
    public int update() {
        log.info("-----------数据库更新 已执行---------");
        return 0;
    }

}
