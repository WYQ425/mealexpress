package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO 分类名称和分类类型可选
     * @return 分类名称和分类类型可选
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        //调用分页查询插件
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        //执行查询sql（动态的）返回封装好的插件的Page泛型为Entity
        Page<Category> page=categoryMapper.page(categoryPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据类型查询分类
     * @param type 类型1为菜品分类，2为套餐分类
     * @return 返回集合泛型为分类的pojo
     */
    @Override
    public List<Category> list(Integer type) {
        //直接执行sql返回结果集合
        return categoryMapper.list(type);
    }

    /**
     * 新增分类
     * @param categoryDTO 核心数据
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        //先将属性复制到Entity
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //状态属性默认禁用
        category.setStatus(StatusConstant.DISABLE);
        //调用mapper执行sql
        categoryMapper.insert(category);
    }

    /**
     * 根据id删除分类
     * @param id 需要删除分类的id
     */
    @Override
    public void deleteById(Long id) {
        //查询该分类关联相关菜品数量
        Integer count=dishMapper.countByCategoryId(id);
        //若该分类下包含菜品，抛出相关异常
        if(count>0) throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);

        //查询该分类关联相关套餐数量
        count=setMealMapper.countByCategoryId(id);
        //若该分类下包含套餐，抛出相关异常
        if(count>0) throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);

        //未关联菜品和套餐，可以删除
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类信息
     * @param categoryDTO 修改后的信息
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        //先将属性复制到Entity
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //调用mapper执行sql
        categoryMapper.update(category);
    }

    /**
     * 根据id修改对应分类的status
     * @param id 修改分类的id
     * @param status 修改后的status
     */
    @Override
    public void status(Long id, Integer status) {
        //创建一个Entity赋值id和status
        Category category=Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }
}
