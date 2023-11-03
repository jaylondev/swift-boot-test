package io.github.jaylondev.swift.boot.test.sample.api.service.biz.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author jaylon 2023/10/22 15:20
 */
@Slf4j
@Service
public class BizServiceCImpl implements BizServiceC, InitializingBean {


    @Override
    public void serviceC() {
        log.info("-----------BizServiceCCCCCC 已执行---------");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("------------BizServiceCCCCCC--初始化--------------");
    }
}
