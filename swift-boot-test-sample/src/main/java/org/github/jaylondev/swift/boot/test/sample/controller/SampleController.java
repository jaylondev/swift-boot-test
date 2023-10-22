package org.github.jaylondev.swift.boot.test.sample.controller;

import org.github.jaylondev.swift.boot.test.sample.dto.resp.SampleResp;
import org.github.jaylondev.swift.boot.test.sample.service.SampleService;
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
    public SampleResp sampleGet(String reqStr) {
        return sampleService.sampleGet(reqStr);
    }
}
