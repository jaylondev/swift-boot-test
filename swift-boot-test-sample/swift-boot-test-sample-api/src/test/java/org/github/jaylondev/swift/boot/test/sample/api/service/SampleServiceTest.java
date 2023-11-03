package org.github.jaylondev.swift.boot.test.sample.api.service;

import lombok.extern.slf4j.Slf4j;
import org.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;
import org.github.jaylondev.swift.boot.test.sample.api.base.BaseTest;
import org.github.jaylondev.swift.boot.test.sample.api.dal.repository.SampleRepositoryA;
import org.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleMapperA;
import org.github.jaylondev.swift.boot.test.sample.api.dto.resp.SampleResp;
import org.github.jaylondev.swift.boot.test.sample.api.utils.JsonUtils;
import org.github.jaylondev.swift.boot.test.utils.MockUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
    @Mock
    private SampleMapperA sampleMapperA;

    @Test
    public void testSampleGet() {
        // mock database
        MockUtils.mock(sampleRepositoryA, sampleMapperA);
        Mockito.doAnswer(invocationOnMock -> {
            log.info("[-----------Mock数据库更新 已执行---------]");
            return 1;
        }).when(sampleMapperA).update();
        // run test
        SampleResp sampleResp = sampleService.sampleGet("test");
        log.info("[测试结果]:{}", JsonUtils.toJson(sampleResp));
    }

}
