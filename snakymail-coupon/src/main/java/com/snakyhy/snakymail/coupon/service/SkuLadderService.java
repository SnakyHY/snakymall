package com.snakyhy.snakymail.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:03:57
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

