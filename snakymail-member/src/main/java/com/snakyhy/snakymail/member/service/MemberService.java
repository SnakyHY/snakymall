package com.snakyhy.snakymail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:16:44
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

