package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setmealMapper;

    /**
     * 添加购物车
     * 先查，有则更改数量+1，无则新增数量=1
     * 新增时需要差别的表获取完整信息
     * @param shoppingCartDTO 添加菜品参数（均可选，包含口味、菜品id、套餐id）
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //复制DTO属性给entity
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        //获取当前用户id并查询购物车数据是否已存在将添加商品
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList != null && shoppingCartList.size()==1){
            //已存在，更新数量+1
            ShoppingCart updateShoppingCart = shoppingCartList.get(0);
            updateShoppingCart.setNumber(updateShoppingCart.getNumber()+1);
            shoppingCartMapper.updateById(updateShoppingCart);
        }else {
            //新数据，分菜品还是套餐补全数据后新增数量=1,创建时间为now
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            Long dishId = shoppingCart.getDishId();
            if(dishId!=null){
                //添加的是菜品，查表获取补全数据
                DishVO dish = dishMapper.getById(dishId);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                //添加的是套餐，查表获取补全数据
                SetmealVO setMeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setImage(setMeal.getImage());
                shoppingCart.setName(setMeal.getName());
                shoppingCart.setAmount(setMeal.getPrice());
            }
            //数据已补全，新增购物车数据
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看请求用户的购物车
     * @return 返回所有请求用户的购物车数据集合
     */
    @Override
    public List<ShoppingCart> list() {
        //获取用户id并调用Mapper(规范是builder一个entity来查)
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.getByUserId(userId);
    }

    /**
     * 清空购物车商品
     */
    @Override
    public void cleanShoppingCart() {
        //获取用户id并调用Mapper进行删除
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中一个商品
     * 先查，数量不是1则更改数量-1，为1则直接删除
     * @param shoppingCartDTO 删除商品参数（均可选，包含口味、菜品id、套餐id）
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //复制DTO属性给entity
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        //获取当前用户id并查询购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);

        if(shoppingCartList!=null&& !shoppingCartList.isEmpty()){
            //获取该商品的数量
            ShoppingCart deleteShoppingCart = shoppingCartList.get(0);
            Integer number = deleteShoppingCart.getNumber();
            if(number==1){
                //如果商品数量为1，删除该商品
                shoppingCartMapper.deleteById(deleteShoppingCart.getId());
            }else {
                //该商品数量>1，数量-1后更新
                deleteShoppingCart.setNumber(deleteShoppingCart.getNumber()-1);
                shoppingCartMapper.updateById(deleteShoppingCart);
            }
        }
    }
}
