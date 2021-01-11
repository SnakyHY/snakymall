package com.snakyhy.snakymail.product.service.impl;

import com.snakyhy.snakymail.product.entity.CategoryBrandRelationEntity;
import com.snakyhy.snakymail.product.service.CategoryBrandRelationService;
import com.snakyhy.snakymail.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
            categoryEntity.setChildren(searchChildren(categoryEntity, entities));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
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
        List<Long> path = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        Long catId = category.getCatId();
        String name = category.getName();
        categoryBrandRelationService.updateCategory(catId, name);
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> cid = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return cid;
    }

    /**
     * 获取三级分类
     *
     * @return
     */
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        /**
         * db优化
         * 将数据的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //查出所有1级分类
        List<CategoryEntity> level1Categories = getParent_cid(selectList,0L);

        //封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.查到二级分类
            List<CategoryEntity> category2Entities = getParent_cid(selectList,v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (category2Entities != null) {
                catalog2Vos = category2Entities.stream().map((category2Entity) -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, category2Entity.getCatId().toString(), category2Entity.getName());
                    //1.找当前2级分类的三级分类
                    List<CategoryEntity> category3Entities = getParent_cid(selectList,category2Entity.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos=null;
                    if (category3Entities != null) {
                        catalog3Vos = category3Entities.stream().map((category3Entity) -> {
                            return new Catalog2Vo.Catalog3Vo(category2Entity.getCatId().toString(), category3Entity.getCatId().toString(), category3Entity.getName());
                        }).collect(Collectors.toList());
                    }
                    catalog2Vo.setCatalog3List(catalog3Vos);
                    return catalog2Vo;

                }).collect(Collectors.toList());
            }

            return catalog2Vos;
        }));
        return parent_cid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        //收集当前节点id
        path.add(catelogId);
        CategoryEntity entity = this.getById(catelogId);
        if (entity.getParentCid() != 0) {
            findParentPath(entity.getParentCid(), path);
        }
        return path;
    }

    //获取子分类（递归调用）
    private List<CategoryEntity> searchChildren(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> childrenEntites = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren((searchChildren(categoryEntity, all)));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return childrenEntites;
    }

}