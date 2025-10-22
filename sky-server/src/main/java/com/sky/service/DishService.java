package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO 前端传递的新增的菜品信息
     */
    void save(DishDTO dishDTO);

    /**
     * 根据id集合删除菜品
     * @param ids id集合
     */
    void delete(List<Long> ids);

    /**
     * 根据id查询菜品基本信息和口味信息
     * @param id 菜品id
     * @return 返回VO
     */
    DishVO getById(Long id);

    /**
     * 根据分类id查询菜品基本信息
     *
     * @param categoryId 分类id
     * @return 返回Dish集合
     */
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 根据分页查询参数进行菜品分页查询
     * @param dishPageQueryDTO 分页查询参数
     * @return PageResult
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜品基本信息和口味数据
     * @param dishDTO 修改的信息
     */
    void update(DishDTO dishDTO);

    /**
     * 修改菜品状态
     * @param id 修改菜品的id
     * @param status 修改后的状态
     */
    void status(Long id, Integer status);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
