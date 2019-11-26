package com.atguigu.guli.service.oss.service;

import java.io.InputStream;

public interface FileService {
    /**
     * 文件上传至阿里云
     * @param inputStream 输入流
     * @param module 模块
     * @param originalFilename 文件原始名
     * @return 文件在oss上的url地址
     */

    String upload(InputStream inputStream, String module, String originalFilename);

    void removeFile(String url);
}
