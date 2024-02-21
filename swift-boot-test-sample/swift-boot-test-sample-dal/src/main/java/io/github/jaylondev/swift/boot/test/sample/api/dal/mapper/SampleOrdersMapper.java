package io.github.jaylondev.swift.boot.test.sample.api.dal.mapper;

import org.apache.ibatis.annotations.Mapper;
import io.github.jaylondev.swift.boot.test.sample.api.dal.entity.SampleOrders;

/**
*  Mapper
*
* @author jaylondev
* @since 2023-11-16 16:16
*/
@Mapper
public interface SampleOrdersMapper {

    /**
     * 根据主键id查询
     *
     * @param id
     * @return 记录信息
     */
    SampleOrders selectByPrimaryKey(Long id);

    /**
     * 根据主键删除数据
     *
     * @param id
     * @return 数量
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(SampleOrders record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(SampleOrders record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(SampleOrders record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(SampleOrders record);
}
