package com.xin.yygh.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class StudentReadListener extends AnalysisEventListener<Student> {

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("标题为 : " + headMap);
    }

    // 每次读取到一行数组之后执行的方法
    @Override
    public void invoke(Student object, AnalysisContext analysisContext) {
        System.out.println(object);
    }

    // 当读取完之后执行的方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("读取完了excel");
    }
}
