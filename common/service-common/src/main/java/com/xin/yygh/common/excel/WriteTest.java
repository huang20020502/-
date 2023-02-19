package com.xin.yygh.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.ExcelBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;
import java.util.List;

public class WriteTest {

    public static void main(String[] args) {

        // 1. 指定写入文件路径
        String fileName = "C:\\Users\\xin\\Desktop\\abc.xlsx";

        // 2. 创建数据
        List<Student> students = new ArrayList<>();
        students.add(new Student(1,"tom",18));
        students.add(new Student(2,"jack",20));
        students.add(new Student(3,"smith",22));

        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(5,"张三",24));
        teachers.add(new Teacher(6,"李四",26));


        // 3. 写入数据

        // 3.1
        ExcelWriter e = EasyExcel.write(fileName).build();

        // 3.2
        WriteSheet sheet1 = EasyExcel.writerSheet(0,"学生表").head(Student.class).build();
        e.write(students,sheet1);

        WriteSheet sheet2 = EasyExcel.writerSheet(1,"老师表").head(Teacher.class).build();
        e.write(teachers,sheet2);

        // 3.4
        e.finish();

//        EasyExcel.write(fileName,Student.class).sheet(0).doWrite(students);

//        ExcelWriter e = EasyExcel.write(fileName, Student.class).build();
//
//        WriteSheet sheet1 = EasyExcel.writerSheet(0, "学生1").build();
//        WriteSheet sheet2 = EasyExcel.writerSheet(1, "学生2").build();
//
//        e.write(students,sheet1);
//        e.write(studentArrayList,sheet2);
//
//        e.finish();


    }
}
