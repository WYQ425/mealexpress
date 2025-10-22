package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO 添加菜品参数（均可选，包含口味、菜品id、套餐id）
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看请求用户的购物车
     * @return 返回所有请求用户的购物车数据集合
     */
    List<ShoppingCart> list();

    /**
     * 清空购物车商品
     */
    void cleanShoppingCart();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO 删除商品参数（均可选，包含口味、菜品id、套餐id）
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);

}
