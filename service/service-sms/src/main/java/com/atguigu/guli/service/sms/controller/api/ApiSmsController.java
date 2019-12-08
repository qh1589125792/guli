package com.atguigu.guli.service.sms.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.RandomUtil;
import com.atguigu.guli.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(description = "短信管理")
@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("send/{phone}")
    public R getCode(@PathVariable String phone){

        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)){//短信尚未过期
            return R.ok();
        }

        //生成验证码并发送
        code = RandomUtil.getFourBitRandom();
        HashMap<String, Object> param = new HashMap<>();
        param.put("code", code);
        smsService.send(phone, param);

        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        return R.ok().message("短信发送成功");
    }
}