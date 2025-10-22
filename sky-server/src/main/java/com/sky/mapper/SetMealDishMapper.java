package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id集合进行分组查询
     * @param dishIds 菜品id集合
     * @return 返回聚合函数count的值
     */
    Integer countByDishIdForBatch(List<Long> dishIds);

    /**
     * 批量新增套餐菜品关系信息
     * @param setMealDishes 菜品套餐关系信息集合
     */
    void insertForBatch(List<SetmealDish> setMealDishes);

    /**
     * 通过套餐id查询套餐菜品关系表和菜品表获取菜品集合
     * @param id 套餐id
     * @return 菜品集合
     */
    List<Dish> getDishes(Long id);

    /**
     * 根据套餐id集合批量删除套餐-菜品关系
     * @param setMealIds 套餐id集合
     */
    void deleteBatch(List<Long> setMealIds);

    /**
     * 通过套餐id查询套餐数量关系
     * @param id 套餐id
     * @return 关系集合
     */
    List<SetmealDish> getBySid(Long id);

    /**
     * 通过套餐id删除关系
     * @param id 套餐id
     */
    void deleteBySid(Long id);

    /**
     * 通过菜品id获取套餐集合(多表联查)
     * @param id 菜品id
     * @return 套餐集合
     */
    List<Setmeal> getsmlByDishId(Long id);
}
