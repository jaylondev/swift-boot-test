package io.github.jaylondev.swift.boot.test.sample.api.dal.repository.impl;

import io.github.jaylondev.swift.boot.test.sample.api.dal.entity.SampleOrders;
import io.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleOrdersMapper;
import io.github.jaylondev.swift.boot.test.sample.api.dal.repository.SampleRepositoryA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jaylon 2023/11/22 10:57
 */
@Repository
public class SampleRepositoryAImpl implements SampleRepositoryA {

    @Autowired
    private SampleOrdersMapper sampleOrdersMapper;

    @Override
    public int insertOne(SampleOrders orders) {
        if (orders == null) {
            return 0;
        }
        return sampleOrdersMapper.insert(orders);
    }
}
