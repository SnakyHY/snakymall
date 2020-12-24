package com.snakyhy.snakymail.order.dao;

import com.snakyhy.snakymail.order.entity.RefundInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款信息
 * 
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:23:41
 */
@Mapper
public interface RefundInfoDao extends BaseMapper<RefundInfoEntity> {
	
}
