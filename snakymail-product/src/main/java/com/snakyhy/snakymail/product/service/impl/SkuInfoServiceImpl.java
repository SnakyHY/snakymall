package com.snakyhy.snakymail.product.service.impl;

import com.snakyhy.snakymail.product.entity.SkuImagesEntity;
import com.snakyhy.snakymail.product.entity.SpuInfoDescEntity;
import com.snakyhy.snakymail.product.service.*;
import com.snakyhy.snakymail.product.vo.SkuItemSaleAttrVo;
import com.snakyhy.snakymail.product.vo.SkuItemVo;
import com.snakyhy.snakymail.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.Query;

import com.snakyhy.snakymail.product.dao.SkuInfoDao;
import com.snakyhy.snakymail.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min) && !"0".equalsIgnoreCase(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && !"0".equalsIgnoreCase(max)) {
            wrapper.le("price", max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo vo = new SkuItemVo();
        //sku基础信息
        SkuInfoEntity byId = this.getById(skuId);
        Long catalogId = byId.getCatalogId();
        Long idSpuId = byId.getSpuId();
        vo.setInfo(byId);
        //图片信息
        List<SkuImagesEntity> images=skuImagesService.getImagesBySkuId(skuId);
        vo.setImages(images);
        //spu销售属性
        List<SkuItemSaleAttrVo> attrVos=skuSaleAttrValueService.getSaleAttrsBySpuId(idSpuId);
        vo.setSaleAttr(attrVos);
        //spu的介绍
        Long spuId = byId.getSpuId();
        SpuInfoDescEntity descEntity = spuInfoDescService.getById(spuId);
        vo.setDesc(descEntity);
        //spu规格参数
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos=attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        vo.setGroupAttrs(spuItemAttrGroupVos);

        return vo;
    }

}