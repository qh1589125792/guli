package com.atguigu.guli.service.order.mapper;

import com.atguigu.guli.service.order.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * 订单 Mapper 接口
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-10
 */
public interface OrderMapper extends BaseMapper<Order> {

    Integer selectCountByCourseId(
            @Param(value="memberId") String memberId,
            @Param(value="courseId") String courseId);

}
