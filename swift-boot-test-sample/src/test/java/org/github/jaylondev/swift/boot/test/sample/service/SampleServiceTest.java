package org.github.jaylondev.swift.boot.test.sample.service;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;
import org.github.jaylondev.swift.boot.test.sample.base.BaseTest;
import org.github.jaylondev.swift.boot.test.sample.dto.resp.SampleResp;
import org.github.jaylondev.swift.boot.test.sample.utils.JsonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jaylon 2023/10/22 11:34
 */
@SwiftBootTest(targetClass = SampleService.class)
@Slf4j
public class SampleServiceTest extends BaseTest {

    @Autowired
    private SampleService sampleService;

    @Test
    public void testSampleGet() {
        SampleResp sampleResp = sampleService.sampleGet("test");
        log.info("[测试结果]:{}", JsonUtils.toJson(sampleResp));
    }

}
