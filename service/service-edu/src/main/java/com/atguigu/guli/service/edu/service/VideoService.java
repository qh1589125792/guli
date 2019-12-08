package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.form.VideoInfoForm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author Qinhan
 * @since 2019-11-20
 */
public interface VideoService extends IService<Video> {

    void saveVideoInfo(VideoInfoForm videoInfoForm);

    VideoInfoForm getVideoInfoFormById(String id);

    void updateVideoInfoById(VideoInfoForm videoInfoForm);

    void removeVideoById(String id);
}
