package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.client.OssClient;
import com.atguigu.guli.service.edu.client.VodClient;
import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.CourseDescription;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.CoursePublishVo;
import com.atguigu.guli.service.edu.entity.vo.CourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.mapper.ChapterMapper;
import com.atguigu.guli.service.edu.mapper.CourseDescriptionMapper;
import com.atguigu.guli.service.edu.mapper.CourseMapper;
import com.atguigu.guli.service.edu.mapper.VideoMapper;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Qinhan
 * @since 2019-11-20
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private OssClient ossClient;

    @Autowired
    private VodClient vodClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        course.setStatus(Course.COURSE_DRAFT);
        BeanUtils.copyProperties(courseInfoForm,course);
        baseMapper.insert(course);

        //保存课程详细信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);
        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoFormById(String id) {
        //从course表中取数据
        Course course = baseMapper.selectById(id);
        //从course_desrcription表中取数据
        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);

        //创建courseInfoForm对象
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course,courseInfoForm);
//        BeanUtils.copyProperties(courseDescription,courseInfoForm);
        courseInfoForm.setDescription(courseDescription.getDescription());
        return courseInfoForm;
    }

    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {

        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm,course);
        baseMapper.updateById(course);
        //从course_desrcription表中取数据
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.updateById(courseDescription);



    }

    @Override
    public void selectPage(Page<Course> pageParam, CourseQueryVo courseQueryVo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("gmt_create");

        if (courseQueryVo == null){
            baseMapper.selectPage(pageParam, queryWrapper);
            return;
        }

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();

        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }

        if (!StringUtils.isEmpty(teacherId) ) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("subject_parent_id", subjectParentId);
        }

        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("subject_id", subjectId);
        }

        baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeCourseById(String id) {
        //根据课程id删除阿里云课程视频
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("video_source_id");
        queryWrapper.eq("course_id",id);

        List<Map<String, Object>> maps = videoMapper.selectMaps(queryWrapper);
        List<String> videoSourceIdList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            String videoSourceId = (String) map.get("video_source_id");
            videoSourceIdList.add(videoSourceId);
        }
        vodClient.removeVideoByIdList(videoSourceIdList);


        //根据课程id删除阿里云课程封面
        Course course = baseMapper.selectById(id);
        String url = course.getCover();
        ossClient.removeFile(url);

        //根据id删除所有视频:video
        QueryWrapper<Video> queryWrapperVideo = new QueryWrapper<>();
        queryWrapperVideo.eq("course_id",id);
        videoMapper.delete(queryWrapperVideo);

        //根据id删除所有章节:Chapter
        QueryWrapper<Chapter> queryWrapperChapter = new QueryWrapper<>();
        queryWrapperChapter.eq("course_id",id);
        chapterMapper.delete(queryWrapperChapter);

        //删除课程详情
        courseDescriptionMapper.deleteById(id);

        //根据id删除课程
        baseMapper.deleteById(id);

    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public void publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        baseMapper.updateById(course);

    }

    @Override
    public Map<String, Object> webSelectPage(Page<Course> pageParam, WebCourseQueryVo webCourseQueryVo) {

        //创建QueryWrapper对象
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();


        //组装查询条件
        if(!StringUtils.isEmpty(webCourseQueryVo.getSubjectParentId())){
            queryWrapper.eq("subject_parent_id",webCourseQueryVo.getSubjectParentId());
        }

        if(!StringUtils.isEmpty(webCourseQueryVo.getSubjectId())){
            queryWrapper.eq("subject_id",webCourseQueryVo.getSubjectId());
        }

        if(!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())){
            queryWrapper.orderByDesc("buy_count");
        }else if(!StringUtils.isEmpty(webCourseQueryVo.getGmtCreateSort())){
            queryWrapper.orderByDesc("gmt_create");
        }else if(!StringUtils.isEmpty(webCourseQueryVo.getPriceSort())){
            queryWrapper.orderByDesc("price");
        }else {
            queryWrapper.orderByDesc("id");
        }

        //执行查询
        baseMapper.selectPage(pageParam,queryWrapper);

        //组装结果
        List<Course> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        //更新课程浏览数
        Course course = baseMapper.selectById(id);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        //获取课程信息
        return baseMapper.selectWebCourseVoById(id);
    }
}
