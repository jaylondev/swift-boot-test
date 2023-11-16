package io.github.jaylondev.swift.boot.test.sample.api.dal.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import io.github.jaylondev.swift.boot.test.sample.api.dal.enums.OrderStatusEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 
 *
 * @author jaylondev
 * @since 2023-11-16 16:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商品名称
     */
    private String skuName;

    /**
     * 购买数量
     */
    private Integer skuCount;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public SampleOrders(String skuName) {
        this.skuName = skuName;
        this.orderNo = String.valueOf(new Random(1000).nextInt());
        this.skuCount = new Random(10).nextInt();
        this.orderStatus = OrderStatusEnum.INIT.getCode();
        this.createTime = new Date();
        this.updateTime = new Date();
    }

}
