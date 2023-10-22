package org.github.jaylondev.swift.boot.test.sample.dao;

import org.github.jaylondev.swift.boot.test.sample.dao.mapper.SampleMapperA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jaylon 2023/10/6 0:09
 */
@Repository
public class SampleRepositoryA {

    @Autowired
    private SampleMapperA mapperA;

    public int update() {
        return mapperA.update();
    }
}
