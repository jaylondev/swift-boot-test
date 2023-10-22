package org.github.jaylondev.swift.boot.test.sample.service;

import org.github.jaylondev.swift.boot.test.sample.dao.SampleRepositoryA;
import org.github.jaylondev.swift.boot.test.sample.dto.resp.SampleResp;
import org.github.jaylondev.swift.boot.test.sample.service.biz.BizServiceA;
import org.github.jaylondev.swift.boot.test.sample.service.biz.BizServiceB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jaylon 2023/10/5 23:52
 */
@Service
public class SampleService {

    public static final Integer SUCCESS_CODE = 200;
    public static final String SUCCESS_MESSAGE = "请求成功";

    @Autowired
    private BizServiceA bizServiceA;
    @Autowired
    private BizServiceB bizServiceB;
    @Autowired
    private SampleRepositoryA repositoryA;

    public SampleResp sampleGet(String reqStr) {
        // 执行业务服务A
        bizServiceA.serviceA();
        // 执行业务服务B
        bizServiceB.serviceB();
        // 修改数据库
        repositoryA.update();
        return this.buildSuccess(reqStr);
    }

    private SampleResp buildSuccess(String reqStr) {
        return SampleResp.builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE + ":" + reqStr)
                .build();
    }
}
