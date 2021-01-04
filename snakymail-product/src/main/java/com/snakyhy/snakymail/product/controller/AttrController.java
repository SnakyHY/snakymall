package com.snakyhy.snakymail.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.snakyhy.snakymail.product.entity.ProductAttrValueEntity;
import com.snakyhy.snakymail.product.service.ProductAttrValueService;
import com.snakyhy.snakymail.product.vo.AttrRespVo;
import com.snakyhy.snakymail.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.snakyhy.snakymail.product.entity.AttrEntity;
import com.snakyhy.snakymail.product.service.AttrService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.R;



/**
 * 商品属性
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-21 20:17:18
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService attrValueService;

    //product/attr/base/listforspu/{spuId}
    //product/attr/update/{spuId}

    @PostMapping("/update/{spuId}")
    public R updateSpuById(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        attrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){

        List<ProductAttrValueEntity> data= attrValueService.baseAttrListForSpu(spuId);

        return R.ok().put("data", data);
    }
    /**
     * 获取属性的值
     * @param params
     * @param catelogId
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,@PathVariable("catelogId") Long catelogId,
    @PathVariable("attrType") String attrType){

        PageUtils page=attrService.queryBaseAttrPage(params,catelogId,attrType);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
