package com.snakyhy.snakymall.cart.interceptor;

import com.alibaba.nacos.client.utils.StringUtils;
import com.snakyhy.common.constant.AuthServerConstant;
import com.snakyhy.common.constant.CartConstant;
import com.snakyhy.common.vo.MemberResponseVo;
import com.snakyhy.snakymall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行目标方法之前，判断用户的登录状态。并封装传递给目标请求
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal=new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo to=new UserInfoTo();
        HttpSession session = request.getSession();
        MemberResponseVo member= (MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(member!=null){
            //登录
            to.setUserId(member.getId());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies!=null && cookies.length >0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    to.setUserKey(cookie.getValue());
                    to.setTempUser(true);
                }
            }
        }

        //如果没有临时用户，一定保存一个临时用户
        if (StringUtils.isEmpty(to.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            to.setUserKey(uuid);
        }
        //目标方法执行之前
        threadLocal.set(to);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //如果没有临时用户，第一次访问购物车就添加临时用户
        if (!userInfoTo.isTempUser()){
            //持续的延长用户的过期时间
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("snakymall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}
