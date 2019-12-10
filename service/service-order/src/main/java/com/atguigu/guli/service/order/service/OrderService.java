package com.atguigu.guli.service.order.service;

import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.entity.vo.OrderQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-10
 */
public interface OrderService extends IService<Order> {
    String saveOrder(String courseId,String memberId);

    Map<String,Object> selectPage(Page<Order> pageParam, OrderQueryVo orderQueryVo);

    Boolean isBuyByCourseId(String memberId, String courseId);

}
