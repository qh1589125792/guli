package com.atguigu.guli.service.order.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.order.client.CourseFeignClient;
import com.atguigu.guli.service.order.client.MemberFeignClient;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.entity.vo.OrderQueryVo;
import com.atguigu.guli.service.order.mapper.OrderMapper;
import com.atguigu.guli.service.order.service.OrderService;
import com.atguigu.guli.service.order.util.OrderNoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.reflections.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-10
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Autowired
    private MemberFeignClient memberFeignClient;

    /**
     * 会员下单
     * @param courseId
     * @param memberId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveOrder(String courseId, String memberId) {
        CourseDto courseDto = courseFeignClient.getCourseDtoById(courseId);
        if (courseDto == null){
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }
        MemberDto memberDto = memberFeignClient.getMemberDtoByMemberId(memberId);
        if (memberDto == null) {
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }
        //创建订单
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());
        order.setCourseId(courseId);
        order.setCourseTitle(courseDto.getTitle());
        order.setCourseCover(courseDto.getCover());
        order.setTeacherName(courseDto.getTeacherName());
        order.setTotalFee(courseDto.getPrice());
        order.setMemberId(memberId);
        order.setMobile(memberDto.getMobile());
        order.setNickname(memberDto.getNickname());
        order.setStatus(0);
        order.setPayType(1);
        orderMapper.insert(order);
        return order.getId();
    }

    @Override
    public Map<String, Object> selectPage(Page<Order> pageParam, OrderQueryVo orderQueryVo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("gmt_create");

        String memberId = orderQueryVo.getMemberId();
        Integer status = orderQueryVo.getStatus();
        String gmtCreateBegin = orderQueryVo.getGmtCreateBegin();
        String gmtCreateEnd = orderQueryVo.getGmtCreateEnd();

        if(!StringUtils.isEmpty(memberId)) {
            queryWrapper.eq("member_id", memberId);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        if (!StringUtils.isEmpty(gmtCreateBegin)) {
            queryWrapper.ge("gmt_create", gmtCreateBegin);
        }

        if (!StringUtils.isEmpty(gmtCreateEnd)) {
            queryWrapper.le("gmt_create", gmtCreateEnd);
        }

        baseMapper.selectPage(pageParam, queryWrapper);

        List<Order> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    @Override
    public Boolean isBuyByCourseId(String memberId, String courseId) {
        //        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        //        queryWrapper
        //                .eq("member_id", memberId)
        //                .eq("course_id", courseId)
        //                .eq("status", 1); //已支付
        //        Integer count = orderMapper.selectCount(queryWrapper);
        Integer count = orderMapper.selectCountByCourseId(memberId, courseId);
        return count.intValue() > 0;
    }
}
