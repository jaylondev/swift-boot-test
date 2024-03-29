package io.github.jaylondev.swift.boot.test.sample.api.controller;

import io.github.jaylondev.swift.boot.test.sample.api.dto.resp.SampleResp;
import io.github.jaylondev.swift.boot.test.sample.api.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaylon 2023/10/5 23:02
 */
@RestController
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/sample")
    @ResponseBody
    public SampleResp sampleGet(String skuName) {
        return sampleService.sampleGet(skuName);
    }
}
