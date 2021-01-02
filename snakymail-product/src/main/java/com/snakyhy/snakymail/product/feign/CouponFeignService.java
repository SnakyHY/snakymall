package com.snakyhy.snakymail.product.feign;

import com.snakyhy.common.to.SkuReductionTo;
import com.snakyhy.common.to.SpuBoundTo;
import com.snakyhy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("snakymail-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    //@RequiresPermissions("coupon:spubounds:save")
    R saveCouponBounds(@RequestBody SpuBoundTo spuBounds);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
