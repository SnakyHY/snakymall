package com.snakyhy.snakymail.coupon.dao;

import com.snakyhy.snakymail.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:03:57
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
