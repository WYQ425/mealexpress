package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO 分类名称和分类类型可选
     * @return 返回统一结果，泛型是分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("请求分类信息分页查询：{}",categoryPageQueryDTO);
        PageResult pageResult=categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据类型查询分类
     * @param type 1为菜品分类，2为套餐分类
     * @return 返回查询结果集合泛型为分类pojo
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        log.info("请求按类型查询分类：{}",type);
        List<Category> list=categoryService.list(type);
        return Result.success(list);
    }

    /**
     * 新增分类
     * @param categoryDTO 核心数据
     * @return 成功结果
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id 删除分类的id-->Long类型
     * @return 成功结果
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result delete(Long id){
        log.info("根据id删除分类：{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类信息
     * @param categoryDTO 修改后的信息
     * @return 成功结果
     */
    @PutMapping
    @ApiOperation("修改分类信息")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类信息：{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类的状态
     * @param status 分类的状态
     * @param id 修改分类的id
     * @return 成功结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改分类的状态：启用/禁用")
    public Result status(@PathVariable("status") Integer status,Long id){
        log.info("修改id:{}分类的状态：{}",id,status);
        categoryService.status(id,status);
        return Result.success();
    }
}
