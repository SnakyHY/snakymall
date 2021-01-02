package com.snakyhy.snakymail.coupon.service.impl;

import com.snakyhy.common.to.MemberPrice;
import com.snakyhy.common.to.SkuReductionTo;
import com.snakyhy.snakymail.coupon.entity.MemberPriceEntity;
import com.snakyhy.snakymail.coupon.entity.SkuLadderEntity;
import com.snakyhy.snakymail.coupon.service.MemberPriceService;
import com.snakyhy.snakymail.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.Query;

import com.snakyhy.snakymail.coupon.dao.SkuFullReductionDao;
import com.snakyhy.snakymail.coupon.entity.SkuFullReductionEntity;
import com.snakyhy.snakymail.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo to) {
        //6.4.sku的优惠、满减等信息 sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price

        //1.保存满减打折，会员价
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(to.getSkuId());
        skuLadderEntity.setAddOther(to.getCountStatus());
        skuLadderEntity.setDiscount(to.getDiscount());
        skuLadderEntity.setFullCount(to.getFullCount());
        if(skuLadderEntity.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }



        //2.sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setAddOther(to.getCountStatus());
        skuFullReductionEntity.setReducePrice(to.getReducePrice());
        skuFullReductionEntity.setSkuId(to.getSkuId());
        skuFullReductionEntity.setFullPrice(to.getFullPrice());
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }


        //3.sms_member_price
        List<MemberPrice> memberPrice = to.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map((price) -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(to.getSkuId());
            memberPriceEntity.setAddOther(1);
            memberPriceEntity.setMemberPrice(price.getPrice());
            memberPriceEntity.setMemberLevelId(price.getId());
            memberPriceEntity.setMemberLevelName(price.getName());
            return memberPriceEntity;
        }).filter((price)->{
            return price.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);
    }

}