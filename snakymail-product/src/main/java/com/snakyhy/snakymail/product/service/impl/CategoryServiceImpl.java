package com.snakyhy.snakymail.product.service.impl;

import com.snakyhy.snakymail.product.entity.CategoryBrandRelationEntity;
import com.snakyhy.snakymail.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.Query;

import com.snakyhy.snakymail.product.dao.CategoryDao;
import com.snakyhy.snakymail.product.entity.CategoryEntity;
import com.snakyhy.snakymail.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        //技术栈：流式编程（java层面过滤信息)
        //获取全部对象
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //获取所有一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(categoryEntity -> {
            categoryEntity.setChildren(searchChildren(categoryEntity,entities));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());



        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {

        //TODO 1.检查当前删除的菜单是否被其他地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path=new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        Long catId = category.getCatId();
        String name = category.getName();
        categoryBrandRelationService.updateCategory(catId,name);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> path){
        //收集当前节点id
        path.add(catelogId);
        CategoryEntity entity = this.getById(catelogId);
        if(entity.getParentCid()!=0){
            findParentPath(entity.getParentCid(),path);
        }
        return path;
    }

    //获取子分类（递归调用）
    private List<CategoryEntity> searchChildren(CategoryEntity root,List<CategoryEntity> all){

        List<CategoryEntity> childrenEntites = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren((searchChildren(categoryEntity, all)));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return childrenEntites;
    }

}