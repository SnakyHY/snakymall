package com.snakyhy.snakymail.product.web;

import com.snakyhy.snakymail.product.service.SkuInfoService;
import com.snakyhy.snakymail.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;
    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable(value = "skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        SkuItemVo vo=skuInfoService.item(skuId);
        System.out.println("准备查询"+skuId+"的详情信息");
        model.addAttribute("item",vo);
        System.out.println(vo);
        return "item";
    }
}
