package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    /**
     * 新增套餐
     * @param setmealDTO 新增的套餐信息
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 修改套餐的状态
     * @param id 修改的套餐的id
     * @param status 套餐状态，1表示起售，0表示停售
     */
    void status(Long id, Integer status);

    /**
     * 批量删除套餐
     * @param ids 批量删除套餐的id集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO 分页查询参数
     * @return pageResult结结果
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 通过套餐id获取套餐VO信息
     * @param id 套餐id
     * @return VO
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐信息
     * @param setmealDTO 修改的套餐具体信息
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
