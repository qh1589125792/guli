package com.atguigu.guli.service.ucenter.service;

import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.entity.vo.LoginInfoVo;
import com.atguigu.guli.service.ucenter.entity.vo.LoginVo;
import com.atguigu.guli.service.ucenter.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-04
 */
public interface MemberService extends IService<Member> {

    Integer countRegisterByDay(String day);

    /**
     * 注册用户
     * @param registerVo
     */
    void register(RegisterVo registerVo);

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    String login(LoginVo loginVo);

    /**
     * 根据会员id获取登录信息
     * @param memberId
     * @return 用户登录信息
     */
    LoginInfoVo getLoginInfo(String memberId);

    Member getByOpenid(String openid);
}
