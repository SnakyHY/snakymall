package com.snakyhy.snakymall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.snakyhy.common.constant.AuthServerConstant;
import com.snakyhy.common.utils.HttpUtils;
import com.snakyhy.common.utils.R;
import com.snakyhy.snakymall.auth.feign.MemberFeignService;
import com.snakyhy.common.vo.MemberResponseVo;
import com.snakyhy.snakymall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 */
@Controller
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, RedirectAttributes attributes) throws Exception {


        //1. 使用code换取token，换取成功则继续2，否则重定向至登录页
        Map<String, String> query = new HashMap<>();
        query.put("client_id", "3778569469");
        query.put("client_secret", "16dce5655dafdfc768f3faab6266884f");
        query.put("grant_type", "authorization_code");
        query.put("redirect_uri", "http://auth.snakymall.com/oauth2.0/weibo/success");
        query.put("code", code);
        //发送post请求换取token
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token",
                "post", new HashMap<String, String>(), query, new HashMap<String, String>());

        Map<String, String> errors = new HashMap<>();
        if (response.getStatusLine().getStatusCode()==200){
            //获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, new TypeReference<SocialUser>() {
            });
            R login = memberFeignService.oauth2Login(socialUser);
            //2.1 远程调用成功，返回首页并携带用户信息
            if (login.getCode() == 0) {
                MemberResponseVo memberResponseVo = login.getData(new TypeReference<MemberResponseVo>() {
                });
                session.setAttribute(AuthServerConstant.LOGIN_USER,memberResponseVo);
                return "redirect:http://snakymall.com";
            }else {
                //2.2 否则返回登录页
                errors.put("msg", "登录失败，请重试");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.snakymall.com/login.html";
            }
        }else {
            errors.put("msg", "获得第三方授权失败，请重试");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.snakymall.com/login.html";
        }

    }
}
