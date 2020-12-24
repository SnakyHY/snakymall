package com.snakyhy.snakymail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:30:34
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

