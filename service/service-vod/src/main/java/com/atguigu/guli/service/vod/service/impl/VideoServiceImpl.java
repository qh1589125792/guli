package com.atguigu.guli.service.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.*;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import com.atguigu.guli.service.vod.util.AliyunVodSDKUtils;
import com.atguigu.guli.service.vod.util.VodProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VodProperties vodProperties;
    @Override
    public String uploadVideo(InputStream inputStream, String originalFilename) {
        String title = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        UploadStreamRequest request = new UploadStreamRequest(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret(),
                title, originalFilename, inputStream);
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);

        String videoId = response.getVideoId();
        if (StringUtils.isEmpty(videoId)){
            log.error("阿里云上传失败" + response.getCode() + "-" + response.getMessage());
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }

    @Override
    public void removeVideo(String videoId) throws ClientException {
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(videoId);
        /*DeleteVideoResponse response = */client.getAcsResponse(request);

    }

    @Override
    public Map<String, Object> getVideoUploadAuthAndAddress(String title, String fileName) throws ClientException {
        //初始化client对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());

        //创建请求对象
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setTitle(title);
        request.setFileName(fileName);

        //获取响应
        CreateUploadVideoResponse response = client.getAcsResponse(request);
        Map<String, Object> map = new HashMap<>();
        map.put("videoId",response.getVideoId());
        map.put("uploadAddress",response.getUploadAddress());
        map.put("uploadAuth",response.getUploadAuth());
        return map;
    }

    @Override
    public Map<String, Object> refreshVideoUploadAuth(String videoId) throws ClientException {
        //初始化client对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());

        //创建请求对象
        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        request.setVideoId(videoId);

        //获取响应
        RefreshUploadVideoResponse response = client.getAcsResponse(request);

        Map<String, Object> map = new HashMap<>();
        map.put("videoId", response.getVideoId());
        map.put("uploadAddress", response.getUploadAddress());
        map.put("uploadAuth", response.getUploadAuth());

        return map;
    }

    @Override
    public String getVideoPlayAuth(String videoSourceId) throws ClientException {
        //初始化client对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());

        //创建请求对象
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoSourceId);

        //获取响应
        GetVideoPlayAuthResponse response = client.getAcsResponse(request);

        return response.getPlayAuth();
    }

    @Override
    public void removeVideoByIdList(List<String> videoSourceIdList) throws ClientException {

        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret()
        );
        DeleteVideoRequest request = new DeleteVideoRequest();
        int size = videoSourceIdList.size();
        StringBuffer idListStr = new StringBuffer();
        for (int i = 0; i < size; i++) {
            idListStr.append(videoSourceIdList.get(i));
            if(i == size-1 || i % 20 == 19){
                System.out.println("idListStr = " + idListStr.toString());
                //支持传入多个视频，多个用逗号分隔，最多20个
                request.setVideoIds(idListStr.toString());
                DeleteVideoResponse acsResponse = client.getAcsResponse(request);
                System.out.println("requestId" + acsResponse.getRequestId());
                idListStr = new StringBuffer();
                System.out.println("idListStr empty = " + idListStr);
            }else if(i % 20 <19){
                idListStr.append(",");
            }
        }
    }
}

