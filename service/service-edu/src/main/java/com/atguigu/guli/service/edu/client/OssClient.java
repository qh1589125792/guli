package com.atguigu.guli.service.edu.client;

import com.atguigu.guli.common.base.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("guli-oss")
public interface OssClient {
    @DeleteMapping("/admin/oss/file/remove")
    public R removeFile(
            @ApiParam(name = "url",value = "要删除的文件路径",required = true)
            @RequestParam("url") String url);

}
