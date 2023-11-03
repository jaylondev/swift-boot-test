package org.github.jaylondev.swift.boot.test.sample.api.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jaylon 2023/10/5 23:43
 */
@Data
@Builder
public class SampleResp implements Serializable {

    private Integer code;

    private String message;

}
