package com.snakyhy.snakymail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.snakymail.member.entity.MemberEntity;
import com.snakyhy.snakymail.member.exception.PhoneExistException;
import com.snakyhy.snakymail.member.exception.UserNameExistException;
import com.snakyhy.snakymail.member.vo.MemberLoginVo;
import com.snakyhy.snakymail.member.vo.MemberRegistVo;
import com.snakyhy.snakymail.member.vo.SocialUser;

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

    void regist(MemberRegistVo registVo);

    void checkPhoneUnique(String phone) throws PhoneExistException;
    void checkUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser);
}

