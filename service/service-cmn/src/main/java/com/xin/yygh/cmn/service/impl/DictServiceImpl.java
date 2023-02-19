package com.xin.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xin.yygh.cmn.listener.DictReadListener;
import com.xin.yygh.cmn.service.DictService;
import com.xin.yygh.cmn.mapper.DictMapper;
import com.xin.yygh.hosp.model.cmn.Dict;
import com.xin.yygh.hosp.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
* @author xin
* @description 针对表【dict(组织架构表)】的数据库操作Service实现
* @createDate 2023-02-01 19:18:43
*/
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
    implements DictService{


    @Autowired
    private DictMapper dictMapper;

    /**
     * 查询父id为 pid值的
     * @param pid 父id
     * @return list
     */
    @Override
    public List<Dict> getChildrenList(Long pid) {
        // 1. 构建查询条件
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",pid);

        // 2. 查询
        List<Dict> childrenData = dictMapper.selectList(wrapper);

        // 3. 遍历数据，查看是否有子元素
        for (Dict dict : childrenData) {
            dict.setHasChildren(hasChildren(dict.getId()));
        }
        return childrenData;
    }


    @Override
    public void downloadDict(HttpServletResponse response) throws Exception {
        // 1. 查询数据库
        List<Dict> dictList = dictMapper.selectList(null);

        List<DictEeVo> data = new ArrayList<>();

        // 2. 将数据封装成DictEevo对象
        for (Dict dict : dictList) {
            DictEeVo vo = new DictEeVo();
            // 这种方式转换，需要目标对象 和 转换对象的属性名是一致的
            BeanUtils.copyProperties(dict,vo);

            data.add(vo);
        }

        System.out.println(data);

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        // 3. 输出文件
        EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet(0,"数据字典").doWrite(data);
    }

    @Override
    public void upload(MultipartFile file) throws IOException {
        // 1. 读取文件
        InputStream is = file.getInputStream();
        EasyExcel.read(is,DictEeVo.class,new DictReadListener(dictMapper)).sheet(0).doRead();
    }


    @Override
    public Long getIdByDictCode(String dictCode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>().eq("dict_code", dictCode);
        Dict dict = dictMapper.selectOne(queryWrapper);
        return dict.getId();
    }

    @Override
    public String getNameByValue(Long value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>().eq("value", value);
        Dict dict = dictMapper.selectOne(queryWrapper);
        return dict.getName();
    }

    @Override
    public String getNameByPidAndValue(Long pid, Long value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>().eq("value", value).eq("parent_id",pid);
        Dict dict = dictMapper.selectOne(queryWrapper);
        return dict.getName();
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        // 1.先查找出dictCode对应的id值
        Long id = this.getIdByDictCode(dictCode);
        // 2.查找父id为 查询处理id的元素
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> list = dictMapper.selectList(wrapper);
        return list;
    }


    private boolean hasChildren(Long id) {
        // 1. 构建查询条件
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);

        // 2. 查询
        return dictMapper.selectCount(wrapper) > 0 ? true : false;
    }
}




