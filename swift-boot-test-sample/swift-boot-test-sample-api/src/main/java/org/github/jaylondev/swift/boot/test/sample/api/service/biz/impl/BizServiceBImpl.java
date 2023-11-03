package org.github.jaylondev.swift.boot.test.sample.api.service.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceB;
import org.springframework.stereotype.Service;

/**
 * @author jaylon 2023/10/6 0:01
 */
@Service
@Slf4j
public class BizServiceBImpl implements BizServiceB {

    @Override
    public void serviceB() {
        log.info("-----------BizServiceBBBBBB 已执行---------");
    }

}
