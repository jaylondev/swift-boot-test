package io.github.jaylondev.swift.boot.test.collect;

import org.springframework.core.Ordered;

/**
 * @author jaylon 2023/8/7 22:25
 */
public interface ICollectHandler extends Ordered {

    void collect(CollectContext collectContext);

}
