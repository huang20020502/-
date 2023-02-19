package com.xin.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xin.yygh.cmn.mapper.DictMapper;
import com.xin.yygh.hosp.model.cmn.Dict;
import com.xin.yygh.hosp.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class DictReadListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    private List<Dict> list = new ArrayList<>();

    public DictReadListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        if (list.size() == 10) {
            // 当list中有10条数据就提交到数据库中
            execute(list);
        }
        Dict dict = new Dict();
        // 将 DictEevo转换成 dict对象
        BeanUtils.copyProperties(dictEeVo, dict);
        list.add(dict);
    }

    private void execute(List<Dict> list) {
        for (Dict dict : list) {
            // 1. 查询数据库 判断是否存在
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("id",dict.getId());
            Integer count = dictMapper.selectCount(wrapper);

            if (count > 0) {
                // 修改操作
                dictMapper.updateById(dict);
            } else {
                // 添加操作
                dictMapper.insert(dict);
            }
        }
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        execute(list);
    }
}
