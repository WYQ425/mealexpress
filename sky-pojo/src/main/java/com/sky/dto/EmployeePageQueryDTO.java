package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
//对于大多数现代 Web 应用（尤其是基于 HTTP 的前后端分离架构），传输给前端的数据对象通常以 JSON 格式传递，因此不需要实现 Serializable 接口
//但如果涉及到 Java 自身的序列化机制，则需要实现该接口,此处是前后端分离开发采用Restful风格，但依旧添加了
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
