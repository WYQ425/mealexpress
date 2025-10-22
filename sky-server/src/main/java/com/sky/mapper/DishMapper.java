package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id进行分组查询
     * @param categoryId 分类id
     * @return 返回聚合函数count的值
     */
    Integer countByCategoryId(Long categoryId);

    /**
     * 添加菜品基本信息并设置返回自增主键
     * @param dish 菜品基本信息
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 根据id集合查询菜品返回菜品集合
     * @param ids id集合
     * @return 菜品集合
     */
    List<Dish> getByIdForBatch(List<Long> ids);

    /**
     * 根据id集合批量删除菜品
     * @param ids 菜品id集合
     */
    void deleteByIdForBatch(List<Long> ids);

    /**
     * 根据分类id查询菜品
     * @param dish entity
     * @return 菜品集合
     */
    List<Dish> getByCategoryId(Dish dish);

    /**
     * 根据分页查询参数进行分页查询菜品
     *
     * @param dishPageQueryDTO 分页查询参数
     * @return 返回查询结果
     */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜品信息
     * @param dish 菜品信息
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);


    /**
     * 根据id查询菜品基本信息
     * @param id 菜品id
     * @return Entity
     */
    DishVO getById(Long id);

    /**
     * 根据条件统计菜品数量
     * @param map 查询条件（状态、分类id）
     * @return 统计数量
     */
    Integer countByMap(Map map);
}
