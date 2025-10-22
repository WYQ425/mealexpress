package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询
     * @param openId 查询参数
     * @return 返回Entity
     */
    User getByOpenid(String openId);

    /**
     * 新增注册的新用户
     * @param user 用户Entity
     */
    void insert(User user);

    /**
     * 根据userid查询user
     * @param userId 用户id
     * @return 返回用户entity
     */
    User getById(Long userId);

    /**
     * 根据map参数统计用户数量
     * @param map 包含开始时间和结束时间
     * @return 统计数量
     */
    Integer countByMap(Map map);

}
