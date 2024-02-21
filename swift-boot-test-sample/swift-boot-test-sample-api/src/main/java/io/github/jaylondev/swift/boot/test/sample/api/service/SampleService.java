package io.github.jaylondev.swift.boot.test.sample.api.service;

import io.github.jaylondev.swift.boot.test.sample.api.dal.entity.SampleOrders;
import io.github.jaylondev.swift.boot.test.sample.api.dal.repository.SampleRepositoryA;
import io.github.jaylondev.swift.boot.test.sample.api.dto.resp.SampleResp;
import io.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceA;
import io.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceB;
import io.github.jaylondev.swift.boot.test.sample.api.service.biz.BizServiceC;
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


    public SampleResp sampleGet(String skuName) {
        // 执行业务服务A
        bizServiceA.serviceA();
        // 执行业务服务B
        bizServiceB.serviceB();
        // 执行业务服务C
        bizServiceC.serviceC();
        // 写入数据库
        SampleOrders orders = new SampleOrders(skuName);
        repositoryA.insertOne(orders);
        return this.buildSuccess(skuName);
    }

    private SampleResp buildSuccess(String skuName) {
        return SampleResp.builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE + ":订单-" + skuName + " 已创建")
                .build();
    }
}
