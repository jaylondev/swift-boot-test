package io.github.jaylondev.swift.boot.test.sample.api.controller;

import com.alibaba.fastjson.JSON;
import io.github.jaylondev.swift.boot.test.collect.MockIOC;
import io.github.jaylondev.swift.boot.test.sample.api.base.BaseSpyMockTest;
import io.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleOrdersMapper;
import io.github.jaylondev.swift.boot.test.sample.api.dto.resp.SampleResp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;

@Slf4j
public class SampleControllerSpyTest extends BaseSpyMockTest {

    @Spy
    private SampleController sampleController = new SampleController();

    @Test
    public void sampleGetTest() throws Exception {
        mock();
        SampleResp sampleResp = sampleController.sampleGet("123");
        log.info(JSON.toJSONString(sampleResp));
    }

    public void mock() throws Exception {
        MockIOC mockIOC = spyMock(sampleController);
        Mockito.doAnswer(args -> {
            log.info("写入数据库：{}", JSON.toJSONString(args.getArgument(0)));
            return 1;
        }).when(mockIOC.get(SampleOrdersMapper.class)).insert(Mockito.any());
    }

}
