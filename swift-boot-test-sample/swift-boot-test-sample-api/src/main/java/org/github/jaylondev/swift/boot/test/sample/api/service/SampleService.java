package org.github.jaylondev.swift.boot.test.sample.api.service;

import org.github.jaylondev.swift.boot.test.sample.api.dal.repository.SampleRepositoryA;
import org.github.jaylondev.swift.boot.test.sample.api.dto.resp.SampleResp;
import org.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceA;
import org.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceB;
import org.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceC;
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
    private BizServiceC bizServiceC;
    @Autowired
    private SampleRepositoryA repositoryA;


    public SampleResp sampleGet(String reqStr) {
        // 执行业务服务A
        bizServiceA.serviceA();
        // 执行业务服务B
        bizServiceB.serviceB();
        // 执行业务服务C
        bizServiceC.serviceC();
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
