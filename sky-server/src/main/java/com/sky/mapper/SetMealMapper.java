package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetMealMapper {
    /**
     * 根据分类id进行分组查询
     * @param categoryId 分类id
     * @return 返回聚合函数count的值
     */
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增套餐信息并返回id主键
     * @param setmeal 套餐信息
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 修改套餐信息
     * @param setmeal 套餐信息Entity
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 通过id集合批量查找套餐
     * @param ids id集合
     * @return 套餐集合
     */
    List<Setmeal> getByIds(List<Long> ids);

    /**
     * 根据id集合批量删除套餐
     * @param ids id集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 分页查询套餐信息，结合菜品分类表查询
     * @param setmealPageQueryDTO 分页查询参数
     * @return Page对象
     */
    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 通过套餐id获取套餐信息和套餐对应的菜品名(多表查询)
     * @param id 套餐id
     * @return 返回套餐vo
     */
    SetmealVO getById(Long id);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */

    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 根据条件统计套餐数量
     * @param map 查询条件(状态、分类id)
     * @return
     */
    Integer countByMap(Map map);
}
