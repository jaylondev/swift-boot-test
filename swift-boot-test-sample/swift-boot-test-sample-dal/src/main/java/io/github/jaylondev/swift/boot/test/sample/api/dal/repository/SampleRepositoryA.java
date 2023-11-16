package io.github.jaylondev.swift.boot.test.sample.api.dal.repository;

import io.github.jaylondev.swift.boot.test.sample.api.dal.entity.SampleOrders;
import io.github.jaylondev.swift.boot.test.sample.api.dal.mapper.SampleOrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jaylon 2023/10/6 0:09
 */
@Repository
public class SampleRepositoryA {

    @Autowired
    private SampleOrdersMapper sampleOrdersMapper;

    public int insertOne(SampleOrders orders) {
        if (orders == null) {
            return 0;
        }
        return sampleOrdersMapper.insert(orders);
    }
}
