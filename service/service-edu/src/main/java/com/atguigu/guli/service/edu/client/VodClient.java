package com.atguigu.guli.service.edu.client;

import com.atguigu.guli.common.base.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient("guli-vod")
public interface VodClient {
    @DeleteMapping("/admin/vod/video/remove")
    public R removeVideoByIdList(
            @ApiParam(name = "videoSourceId",value = "阿里云视频文件的id",required = true)
            @RequestBody List<String> videoSourceIdList);

}
