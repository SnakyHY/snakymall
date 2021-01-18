package com.snakyhy.snakymail.product;

import com.snakyhy.snakymail.product.dao.AttrGroupDao;
import com.snakyhy.snakymail.product.dao.SkuSaleAttrValueDao;
import com.snakyhy.snakymail.product.entity.BrandEntity;
import com.snakyhy.snakymail.product.service.BrandService;
import com.snakyhy.snakymail.product.service.CategoryService;
import com.snakyhy.snakymail.product.service.SkuSaleAttrValueService;
import com.snakyhy.snakymail.product.vo.SkuItemSaleAttrVo;
import com.snakyhy.snakymail.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class SnakymailProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao dao;

    @Autowired
    SkuSaleAttrValueDao dao2;

    @Test
    void testSale(){
        List<SkuItemSaleAttrVo> saleAttrsBySkuId = dao2.getSaleAttrsBySpuId(3L);
        System.out.println(saleAttrsBySkuId);
    }
    @Test
    void testAttr(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = dao.getAttrGroupWithAttrsBySpuId(3L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    void testRedisson() {
        System.out.println(redissonClient);
    }

    @Test
    void testRedis() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world" + UUID.randomUUID());

        String hello = ops.get("hello");
        System.out.println("hello--->" + hello);
    }

    @Test
    void catIdPath() {
        Long[] catelogPath = categoryService.findCatelogPath(165L);
        log.debug("路径为:" + Arrays.asList(catelogPath));
    }

    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("success!");
    }

}
