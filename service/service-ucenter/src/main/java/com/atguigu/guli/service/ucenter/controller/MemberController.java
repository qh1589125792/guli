package com.atguigu.guli.service.ucenter.controller;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.ucenter.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-04
 */
@CrossOrigin
@RestController
@RequestMapping("/admin/ucenter/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "今日注册")
    @GetMapping(value = "count-register/{day}")
    public R registerCount(
            @ApiParam(name = "day",value = "统计日期")
            @PathVariable("day") String day){
        Integer count = memberService.countRegisterByDay(day);
        return  R.ok().data("countRegister",count);
    }

}

