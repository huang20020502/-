package com.xin.yygh.hosp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @Document : 指定集合，如果没有指定集合就是实体类的类名首字母小写
@Document(value = "user")
public class User {

    // @Id : 将实体类属性映射为mongodb中的_id字段
    @Id
    private String id;

    // @Field : 将实体类属性映射为集合中对应的字段   value : 设置集合的字段名
    @Field(value = "name")
    private String name;

    private Integer age;

    private Boolean gender;
}
