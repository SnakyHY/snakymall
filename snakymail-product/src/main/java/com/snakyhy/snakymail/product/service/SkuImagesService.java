package com.snakyhy.snakymail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-21 19:36:06
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

