package com.snakyhy.snakymail.product.web;

import com.snakyhy.snakymail.product.entity.CategoryEntity;
import com.snakyhy.snakymail.product.service.CategoryService;
import com.snakyhy.snakymail.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String getIndex(Model model) {
        //获取所有的一级分类
        List<CategoryEntity> categories = categoryService.getLevel1Categories();
        model.addAttribute("categories", categories);
        return "index";
    }

//    index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }
}
