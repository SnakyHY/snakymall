package com.snakyhy.snakymail.ware.service.impl;

import com.snakyhy.common.utils.R;
import com.snakyhy.snakymail.ware.feign.ProductFeignService;
import com.snakyhy.snakymail.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.Query;

import com.snakyhy.snakymail.ware.dao.WareSkuDao;
import com.snakyhy.snakymail.ware.entity.WareSkuEntity;
import com.snakyhy.snakymail.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         *    wareId: 123,//仓库id
         *    skuId: 123//商品id
         */
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如果还没有这个库存记录
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字 如果失败整个事务无需回滚
            try{
                R info = productFeignService.info(wareSkuEntity.getSkuId());
                if(info.getCode()==0){
                    Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }
            this.baseMapper.insert(wareSkuEntity);

        }else {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }

    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map((skuId) -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //select SUM(stock-stock_locked) from wms_ware_sku where sku_id=?
            long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count > 0);
            return vo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

}