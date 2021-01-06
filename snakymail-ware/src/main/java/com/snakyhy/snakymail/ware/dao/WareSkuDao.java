package com.snakyhy.snakymail.ware.dao;

import com.snakyhy.snakymail.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:30:34
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    long getSkuStock(@Param("skuId") Long skuId);
}
