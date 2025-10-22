package com.sky.controller.user;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {
    public static final String KEY= MessageConstant.SHOP_STATUS;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取当前店铺状态
     * @return 返回Integer类型的状态结果
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getShopStatus(){
        log.info("客户端申请获取当前店铺状态");
        //获取redis对应key的value
        Integer status =(Integer) redisTemplate.opsForValue().get(KEY);
        log.info("当前店铺状态：{}",status==1?"营业中":"打烊");
        return Result.success(status);
    }
}
