package com.xin.yygh.cmn.service;


import com.baomidou.mybatisplus.extension.service.IService;

import com.xin.yygh.hosp.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
* @author xin
* @description 针对表【dict(组织架构表)】的数据库操作Service
* @createDate 2023-02-01 19:18:43
*/
public interface DictService extends IService<Dict> {


    List<Dict> getChildrenList(Long pid);

    void downloadDict(HttpServletResponse response) throws UnsupportedEncodingException, Exception;

    void upload(MultipartFile file) throws IOException;

    Long getIdByDictCode(String dictCode);

    String getNameByValue(Long value);

    String getNameByPidAndValue(Long pid, Long value);

    List<Dict> findByDictCode(String dictCode);
}
