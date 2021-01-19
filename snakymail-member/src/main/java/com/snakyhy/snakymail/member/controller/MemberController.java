package com.snakyhy.snakymail.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.snakyhy.common.exception.BizCodeEnum;
import com.snakyhy.snakymail.member.exception.PhoneExistException;
import com.snakyhy.snakymail.member.exception.UserNameExistException;
import com.snakyhy.snakymail.member.feign.CouponFeignService;
import com.snakyhy.snakymail.member.vo.MemberLoginVo;
import com.snakyhy.snakymail.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.snakyhy.snakymail.member.entity.MemberEntity;
import com.snakyhy.snakymail.member.service.MemberService;
import com.snakyhy.common.utils.PageUtils;
import com.snakyhy.common.utils.R;



/**
 * 会员
 *
 * @author haoyun
 * @email snakyhy@gmail.com
 * @date 2020-12-22 10:16:44
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){

        MemberEntity memberEntity=memberService.login(vo);
        if (memberEntity!=null){
            return R.ok();
        }else{
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),
                    BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }

    }
    /**
     * 注册
     * @param registVo
     * @return
     */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo registVo) {
        try {
            memberService.regist(registVo);
            //异常机制：通过捕获对应的自定义异常判断出现何种错误并封装错误信息
        } catch (UserNameExistException userException) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        } catch (PhoneExistException phoneException) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @RequestMapping("/coupon/list")
    public R memberCoupon(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("zhangsan");
        return R.ok().put("member",memberEntity).put("coupon",couponFeignService.memberCoupons().get("coupon"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
