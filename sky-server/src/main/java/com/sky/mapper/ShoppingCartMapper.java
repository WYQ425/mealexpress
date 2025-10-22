package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 条件查询购物车数据
     * @param shoppingCart 购物车查询参数
     * @return 返回数据List
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id更新购物车数据
     * @param updateShoppingCart 需要更新的数据
     */
    void updateById(ShoppingCart updateShoppingCart);

    /**
     * 新增购物车数据
     * @param shoppingCart 需要新增的数据
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据userId查询
     * @param userId 当前用户id
     * @return 这个用户的所有购物车数据
     */
    List<ShoppingCart> getByUserId(Long userId);

    /**
     * 根据用户id删除购物车数据
     * @param userId 当前用户id
     */
    void deleteByUserId(Long userId);

    /**
     * 根据id删除购物车商品数据
     * @param id 需要删除的商品的id
     */
    void deleteById(Long id);

    /**
     * 批量添加到购物车
     * @param shoppingCartList 购物车商品集合
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);

}
