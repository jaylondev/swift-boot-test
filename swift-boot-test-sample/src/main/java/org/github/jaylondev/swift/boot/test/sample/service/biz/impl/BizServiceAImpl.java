package org.github.jaylondev.swift.boot.test.sample.service.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.sample.service.biz.BizServiceA;
import org.springframework.stereotype.Service;

/**
 * @author jaylon 2023/10/6 0:00
 */
@Service
@Slf4j
public class BizServiceAImpl implements BizServiceA {

    @Override
    public void serviceA() {
        log.info("-----------BizServiceAAAAAA 已执行---------");
    }
}