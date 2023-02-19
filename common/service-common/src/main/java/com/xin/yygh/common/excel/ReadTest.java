package com.xin.yygh.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

public class ReadTest {

    public static void main(String[] args) {
        // 1. 指定写入文件路径
        String fileName = "C:\\Users\\xin\\Desktop\\abc.xlsx";
//        EasyExcel.read(fileName,Student.class,new StudentReadListener()).sheet(0).doRead();

        // 2. 读取文件
        ExcelReader excelReader = EasyExcel.read(fileName).build();

        // 2.2
        ReadSheet sheet1 = EasyExcel.readSheet(0).head(Student.class).registerReadListener(new StudentReadListener()).build();
        ReadSheet sheet2 = EasyExcel.readSheet(1).head(Teacher.class).registerReadListener(new TeacherReadListener()).build();

        excelReader.read(sheet1,sheet2);
    }
}
