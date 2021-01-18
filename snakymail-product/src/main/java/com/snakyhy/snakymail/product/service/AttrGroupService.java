package com.snakyhy.snakymail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.product.entity.AttrGroupEntity;
import com.snakyhy.snakymail.product.vo.AttrGroupWithAttrsVo;
import com.snakyhy.snakymail.product.vo.SkuItemVo;
import com.snakyhy.snakymail.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-21 19:36:06
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

