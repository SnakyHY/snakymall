package com.snakyhy.snakymail.product.dao;

import com.snakyhy.snakymail.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-21 19:36:06
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrs(@Param("attrsId") List<Long> attrsId);
}
