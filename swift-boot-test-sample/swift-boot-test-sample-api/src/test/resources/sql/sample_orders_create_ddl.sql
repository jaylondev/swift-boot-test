DROP TABLE sample_orders IF EXISTS;
CREATE TABLE `sample_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_no` varchar(100) NOT NULL COMMENT '订单号',
  `sku_name` varchar(100) NOT NULL COMMENT '商品名称',
  `sku_count` int NOT NULL COMMENT '购买数量',
  `order_status` tinyint NOT NULL COMMENT '订单状态',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
);