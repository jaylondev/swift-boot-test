package io.github.jaylondev.swift.boot.test.sample.api.service;

import io.github.jaylondev.swift.boot.test.annotations.DbTest;
import io.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleOrdersMapper;
import lombok.extern.slf4j.Slf4j;
import io.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;
import io.github.jaylondev.swift.boot.test.sample.api.base.BaseTest;
import io.github.jaylondev.swift.boot.test.sample.api.dal.repository.SampleRepositoryA;
import io.github.jaylondev.swift.boot.test.sample.api.dto.resp.SampleResp;
import io.github.jaylondev.swift.boot.test.sample.api.utils.JsonUtils;
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
    @Autowired
    private SampleRepositoryA sampleRepositoryA;

    @Test
    @DbTest( sqlFile = "/db/sample_orders_create_ddl.sql")
    public void testSampleGet() {
        // run test
        SampleResp sampleResp = sampleService.sampleGet("test");
        log.info("[测试结果]:{}", JsonUtils.toJson(sampleResp));
    }

}
