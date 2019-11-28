package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author Qinhan
 * @since 2019-11-20
 */
public interface SubjectService extends IService<Subject> {

    void batchImpor(InputStream inputStream) throws Exception;

    List<SubjectVo> nestedList();
    List<SubjectVo> nestedList2();
}
