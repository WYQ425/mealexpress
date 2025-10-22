package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordEditDTO implements Serializable {

    //员工id,服务器通过线程局部变量获取，不需要在此设置，且前端不传此参数
    //private Long empId;

    //旧密码
    private String oldPassword;

    //新密码
    private String newPassword;

}
