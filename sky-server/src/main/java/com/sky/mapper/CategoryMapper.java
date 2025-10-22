package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO 分类名称和分类类型可选
     * @return 封装好的插件的Page泛型为Entity
     */
    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类
     * @param type 类型1为菜品分类，2为套餐分类
     * @return 返回集合泛型为分类的pojo
     */
    List<Category> list(Integer type);

    /**
     * 新增分类
     * @param category Entity对象
     */
    //自动填充创建时间注解
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    /**
     * 根据id删除分类
     * @param id 需要删除分类的id
     */
    void deleteById(Long id);

    /**
     * 修改分类信息
     * @param category 需要修改的分类实体类
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);
}
