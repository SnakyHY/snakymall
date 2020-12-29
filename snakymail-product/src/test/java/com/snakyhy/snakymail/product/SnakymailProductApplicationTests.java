package com.snakyhy.snakymail.product;

import com.snakyhy.snakymail.product.entity.BrandEntity;
import com.snakyhy.snakymail.product.service.BrandService;
import com.snakyhy.snakymail.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;

@Slf4j
@SpringBootTest
class SnakymailProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;


    @Test
    void catIdPath(){
        Long[] catelogPath = categoryService.findCatelogPath(165L);
        log.debug("路径为:"+ Arrays.asList(catelogPath));
    }
    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("success!");
    }

}
