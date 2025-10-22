package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO 请求参数（均可选，包含口味、菜品id、套餐id）
     * @return 成功结果
     */
    @PostMapping("/add")
    @ApiOperation(("添加购物车"))
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("请求添加购物车：{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return 返回所有请求用户的购物车数据集合
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
        log.info("请求查看购物车");
        List<ShoppingCart> shoppingCartList=shoppingCartService.list();
        return Result.success(shoppingCartList);
    }

    /**
     * 清空购物车商品
     * @return 成功结果
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车商品")
    public Result<String> clean(){
        log.info("请求清空购物车");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO 请求参数（均可选，包含口味、菜品id、套餐id）
     * @return 成功结果
     */
    @PostMapping("/sub")
    @ApiOperation(("删除购物车中一个商品"))
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("请求删除购物车中一个商品：{}",shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
