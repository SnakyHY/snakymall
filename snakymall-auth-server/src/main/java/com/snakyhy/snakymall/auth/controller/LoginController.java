package com.snakyhy.snakymall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.snakyhy.common.constant.AuthServerConstant;
import com.snakyhy.common.exception.BizCodeEnum;
import com.snakyhy.common.utils.R;
import com.snakyhy.snakymall.auth.feign.MemberFeignService;
import com.snakyhy.snakymall.auth.feign.ThirdPartyFeignService;
import com.snakyhy.snakymall.auth.vo.UserLoginVo;
import com.snakyhy.snakymall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeignService memberFeignService;
    /**
     * 利用WebController直接进行视图映射
     */


    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){

        //1.接口防刷



        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() -l < 60000){
                //60秒内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }


        //2.验证码再次校验
        String code = UUID.randomUUID().toString().substring(0, 5);
        String substring = code+"_"+System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,substring,10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone,code);
        return R.ok();
    }

    /**
     * //TODO 重定向携带数据，利用session模拟（分布式session）
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */

    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result,
                         RedirectAttributes redirectAttributes){


        if (result.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            /**
             * 方法一
             * Map<String, String> errors = result.getFieldErrors().stream().map(fieldError ->{
             *                 String field = fieldError.getField();
             *                 String defaultMessage = fieldError.getDefaultMessage();
             *                 errors.put(field,defaultMessage);
             *                 return errors;
             *             }).collect(Collector.asList());
             */
            //方法二：
            //1.1 如果校验不通过，则封装校验结果
            result.getFieldErrors().forEach(item->{
                errors.put(item.getField(), item.getDefaultMessage());
                //1.2 将错误信息封装到session中
            });
            //1.2 将错误信息封装到session中
            redirectAttributes.addFlashAttribute("errors",errors);
            /**
             * 使用 return "forward:/reg.html"; 会出现
             * 问题：Request method 'POST' not supported的问题
             * 原因：用户注册-> /regist[post] ------>转发/reg.html (路径映射默认都是get方式访问的)
             * 校验出错转发到注册页
             */
            //return "reg";    //转发会出现重复提交的问题，不要以转发的方式
            //使用重定向  解决重复提交的问题。但面临着数据不能携带的问题，就用RedirectAttributes
            return "redirect:http://auth.snakymall.com/reg.html";
        }

        //1、校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                //验证码通过,删除缓存中的验证码；令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //真正注册调用远程服务注册
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.snakymall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg",new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.snakymall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错转发到注册页
            return "redirect:http://auth.snakymall.com/reg.html";
        }

        //注册成功回到登录页
        //return "redirect:http://auth.gulimall.com/login.html";
        return "redirect:http://auth.snakymall.com/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo,RedirectAttributes redirectAttributes){
        //远程登录
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0){
            //成功
            return "redirect:http://snakymall.com";
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.snakymall.com/login.html";
        }

    }

}
