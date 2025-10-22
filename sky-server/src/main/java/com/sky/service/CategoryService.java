package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO 分类名称和分类类型可选
     * @return 分页查询结果
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类
     * @param type 类型1为菜品分类，2为套餐分类
     * @return 返回集合泛型为分类的pojo
     */
    List<Category> list(Integer type);

    /**
     * 新增分类
     * @param categoryDTO 核心数据
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @param id 需要删除分类的id
     */
    void deleteById(Long id);

    /**
     * 修改分类信息
     * @param categoryDTO 修改后的信息
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 根据id修改对应分类的status
     * @param id 修改分类的id
     * @param status 修改后的status
     */
    void status(Long id, Integer status);
}
