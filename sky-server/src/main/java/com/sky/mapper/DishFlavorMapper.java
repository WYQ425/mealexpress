package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加口味数据
     * @param flavors 口味数据集合
     */
    void insertForBatch(List<DishFlavor> flavors);

    /**
     * 批量删除口味数据
     * @param dishIds 菜品id集合
     */
    void deleteByIdForBatch(List<Long> dishIds);

    /**
     * 根据菜品id查询口味集合
     * @param dishId 菜品id
     * @return 口味数据集合
     */
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 根据菜品id删除口味数据
     * @param dishId 菜品id
     */
    void deleteByDishId(Long dishId);
}
