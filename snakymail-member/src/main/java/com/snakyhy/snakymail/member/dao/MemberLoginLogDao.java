package com.snakyhy.snakymail.member.dao;

import com.snakyhy.snakymail.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:16:44
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
