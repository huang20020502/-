package com.xin.yygh.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ColumnWidth(value = 50)
public class Teacher{

    @ExcelProperty(value = "编号")
    private Integer id;

    @ExcelProperty(value = "名字")
    private String name;

    @ExcelProperty(value = "年龄")
    private Integer age;

}
