package com.atguigu.guli.service.statistics.service.impl;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.client.UcenterClient;
import com.atguigu.guli.service.statistics.entity.Daily;
import com.atguigu.guli.service.statistics.mapper.DailyMapper;
import com.atguigu.guli.service.statistics.service.DailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author Qinhan
 * @since 2019-12-04
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UcenterClient ucenterClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createStatisticsByDay(String day) {
        //如果当日日期的记录已经存在，则删除重新统计或提示用户当日记录已存在
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_calculated",day);
        baseMapper.delete(queryWrapper);

        //生成统计记录
//        R r = restTemplate.getForObject(
//                "http://guli-ucenter/admin/ucenter/member/count-register/{day}",
//                R.class,
//                day);
//        //System.out.println(r.getData().get("countRegister"));

        R r = ucenterClient.registerCount(day);
        Integer registerNum = (Integer)r.getData().get("countRegister");
        Integer loginNum = RandomUtils.nextInt(100, 200);
        Integer videoViewNum = RandomUtils.nextInt(100, 200);
        Integer courseNum = RandomUtils.nextInt(100, 200);

        Daily daily = new Daily();
        daily.setDateCalculated(day);
        daily.setRegisterNum(registerNum);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);


        baseMapper.insert(daily);


    }

    @Override
    public Map<String, Object> getChartData(String begin, String end, String type) {
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> xList = new ArrayList<>(); //日期列表
        ArrayList<Integer> yList = new ArrayList<>(); //数据列表
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(type,"date_calculated");
        queryWrapper.between("date_calculated",begin,end);

        List<Map<String, Object>> mapsData = baseMapper.selectMaps(queryWrapper);
        for (Map<String, Object> data : mapsData) {
            String dateCalculated = (String) data.get("date_calculated");
            xList.add(dateCalculated);
            Integer count = (Integer) data.get(type);
            yList.add(count);
        }
        map.put("xData",xList);
        map.put("yData",yList);
        return map;
    }
}
