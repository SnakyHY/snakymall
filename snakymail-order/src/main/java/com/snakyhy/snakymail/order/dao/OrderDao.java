package com.snakyhy.snakymail.order.dao;

import com.snakyhy.snakymail.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:23:41
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
