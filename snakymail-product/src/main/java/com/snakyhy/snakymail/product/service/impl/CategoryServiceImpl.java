package com.snakyhy.snakymail.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.snakyhy.snakymail.product.entity.CategoryBrandRelationEntity;
import com.snakyhy.snakymail.product.service.CategoryBrandRelationService;
import com.snakyhy.snakymail.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

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
    @CacheEvict(value = "category",allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        Long catId = category.getCatId();
        String name = category.getName();
        categoryBrandRelationService.updateCategory(catId, name);
    }

    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        System.out.println("getLevel1Categories...");
        List<CategoryEntity> cid = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return cid;
    }


    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库..");
        /**
         * db优化
         * 将数据的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //查出所有1级分类
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.查到二级分类
            List<CategoryEntity> category2Entities = getParent_cid(selectList, v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (category2Entities != null) {
                catalog2Vos = category2Entities.stream().map((category2Entity) -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, category2Entity.getCatId().toString(), category2Entity.getName());
                    //1.找当前2级分类的三级分类
                    List<CategoryEntity> category3Entities = getParent_cid(selectList, category2Entity.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
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

    /**
     * 获取三级分类
     *
     * @return
     */


    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            System.out.println("缓存不命中，查询数据库..");
            //缓存中没有
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            String jsonString = JSON.toJSONString(catalogJsonFromDb);
            //放入缓存
            stringRedisTemplate.opsForValue().set("catalogJson", jsonString, 1, TimeUnit.DAYS);
            return catalogJsonFromDb;
        }
        System.out.println("缓存命中，直接返回..");
        //转为指定的对象
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
        return result;
    }

    /**
     * 分布式锁，占坑操作
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //设置过期时间
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("分布式锁成功");
            //stringRedisTemplate.expire("lock",30,TimeUnit.SECONDS);
            //加锁成功
            Map<String, List<Catalog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //利用lua脚本解锁
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
                // stringRedisTemplate.delete("lock");
            }


            return dataFromDb;
        } else {
            //加锁失败..自旋重试
            System.out.println("加锁失败..自旋重试");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }

    }

    /**
     * 分布式锁，利用redisson
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        System.out.println("分布式锁成功");

        lock.lock();
        Map<String, List<Catalog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;
    }


    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库..");
        /**
         * db优化
         * 将数据的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //查出所有1级分类
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.查到二级分类
            List<CategoryEntity> category2Entities = getParent_cid(selectList, v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (category2Entities != null) {
                catalog2Vos = category2Entities.stream().map((category2Entity) -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, category2Entity.getCatId().toString(), category2Entity.getName());
                    //1.找当前2级分类的三级分类
                    List<CategoryEntity> category3Entities = getParent_cid(selectList, category2Entity.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
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
        String jsonString = JSON.toJSONString(parent_cid);
        //放入缓存
        stringRedisTemplate.opsForValue().set("catalogJson", jsonString, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    public synchronized Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        return getDataFromDb();
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parentCid) {
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

