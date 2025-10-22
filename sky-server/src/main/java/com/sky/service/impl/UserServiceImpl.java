package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    /**
     * 微信登录
     * @param userLoginDTO 授权码
     * @return 返回（已存在/创建的）用户对象
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //获取微信openId
        String openId=getOpenId(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openId==null) throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        //根据openid查询用户
        User user=userMapper.getByOpenid(openId);

        //判断是否已存在用户，如果不存在则是新用户，进行注册
        if(user==null){
            user=User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /**
     * 通过HttpClient向微信Http接口发送请求获取openId
     * @param code 请求参数之一，授权码
     * @return 返回获取到的openId
     */
    private String getOpenId(String code) {
        //创建请求参数appid、secret、js_code，grant_type
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");//默认是authorization_code

        //调用封装好的Util发送请求并接收结果
        String result = HttpClientUtil.doGet(WX_LOGIN, map);

        //解析json数据获取openid并返回
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject.getString("openid");
    }
}
