package io.github.jaylondev.swift.boot.test.sample.api.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author jaylon 2023/11/16 16:11
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    INIT(1, "已下单"),
    ;

    private final Integer code;

    private final String desc;
}
