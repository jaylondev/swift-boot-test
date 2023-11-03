package org.github.jaylondev.swift.boot.test.sample.api.dal.repository;

import org.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleMapperA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jaylon 2023/10/6 0:09
 */
@Repository
public class SampleRepositoryA {

    @Autowired(required = false)
    private SampleMapperA mapperA;

    public int update() {
        return mapperA.update();
    }
}
