package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO 授权码
     * @return 返回（已存在/创建的）用户对象
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
