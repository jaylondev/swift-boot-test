package org.github.jaylondev.swift.boot.test.sample.core.impl;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.sample.core.CoreServiceA;
import org.springframework.stereotype.Service;

/**
 * @author jaylon 2023/10/22 22:12
 */
@Service
@Slf4j
public class CoreServiceAImpl implements CoreServiceA {
    @Override
    public void coreServiceA() {
        log.info("-----------CoreServiceA-执行完成--------------------------");
    }
}
