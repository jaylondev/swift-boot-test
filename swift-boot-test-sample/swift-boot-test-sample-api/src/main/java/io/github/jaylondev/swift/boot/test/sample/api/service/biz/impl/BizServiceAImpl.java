package io.github.jaylondev.swift.boot.test.sample.api.service.biz.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceA;
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
