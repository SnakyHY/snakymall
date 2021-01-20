package com.snakyhy.snakymail.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.StringUtils;
import com.snakyhy.common.utils.HttpUtils;
import com.snakyhy.snakymail.member.dao.MemberLevelDao;
import com.snakyhy.snakymail.member.entity.MemberLevelEntity;
import com.snakyhy.snakymail.member.exception.PhoneExistException;
import com.snakyhy.snakymail.member.exception.UserNameExistException;
import com.snakyhy.snakymail.member.vo.MemberLoginVo;
import com.snakyhy.snakymail.member.vo.MemberRegistVo;
import com.snakyhy.snakymail.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.Query;

import com.snakyhy.snakymail.member.dao.MemberDao;
import com.snakyhy.snakymail.member.entity.MemberEntity;
import com.snakyhy.snakymail.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();
        //设置默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());

        //检查用户名和手机号是否唯一。为了让controller能感知异常，异常机制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());
        //密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        memberDao.insert(entity);
    }


    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0) {
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberDao dao = this.baseMapper;
        //select * from ums_member where username=? OR mobile=?
        MemberEntity memberEntity = dao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).
                or().eq("mobile", loginacct));
        if (memberEntity == null) {
            return null;
        } else {
            String passwordDb = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //2、密码匹配
            boolean matches = passwordEncoder.matches(password, passwordDb);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }

        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        // 1 根据 uid 判断当前用户是否以前用社交平台登录过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", socialUser.getUid()));
        if (memberEntity != null) {
            // 说明这个用户之前已经注册过
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            // 未找到则注册 根据社交平台的开放接口查询用户的开放信息存储到系统
            MemberEntity register = new MemberEntity();
            try {
                Map<String, String> query = new HashMap<>();
                query.put("access_token", socialUser.getAccess_token());
                query.put("uid", socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    // ......
                    register.setNickname(name);
                    register.setGender("m".equals(gender) ? 1 : 0);
                    // .....
                }
            } catch (Exception e) {
            }
            register.setSocialUid(socialUser.getUid());
            register.setAccessToken(socialUser.getAccess_token());
            register.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.insert(register);
            return register;
        }

    }

}