package com.atguigu.guli.service.ucenter.mapper;

import com.atguigu.guli.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-04
 */
public interface MemberMapper extends BaseMapper<Member> {

    Integer selectRegisterCount(String day);

}
