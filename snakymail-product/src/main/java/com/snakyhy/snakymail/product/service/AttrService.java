package com.snakyhy.snakymail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.product.entity.AttrEntity;
import com.snakyhy.snakymail.product.vo.AttrGroupRelationVo;
import com.snakyhy.snakymail.product.vo.AttrRespVo;
import com.snakyhy.snakymail.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-21 19:36:06
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    List<Long> selectSearchAttrs(List<Long> collect);
}

