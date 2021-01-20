package com.snakyhy.snakymall.auth.feign;

import com.snakyhy.common.utils.R;
import com.snakyhy.snakymall.auth.vo.SocialUser;
import com.snakyhy.snakymall.auth.vo.UserLoginVo;
import com.snakyhy.snakymall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("snakymail-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo registVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oauth2Login(@RequestBody SocialUser socialUser);
}
