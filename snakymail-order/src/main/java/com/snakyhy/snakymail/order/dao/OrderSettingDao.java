package com.snakyhy.snakymail.order.dao;

import com.snakyhy.snakymail.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:23:41
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
