package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO 菜品信息
     * @return 成功结果
     */
    @PostMapping
    @ApiOperation("新增菜品")
   @CacheEvict(cacheNames = "dishCache",key="#dishDTO.categoryId")
    /*
     * @CachePut(cacheNames = "dishCache",key="#dishDTO.categoryId")
     * CachePut：将方法返回值放入缓存,因为缓存的是集合，所以新增直接全部删除
     * value：缓存的名称，每个缓存名称下面可以有多个key
     * key：缓存的key
     */
    public Result<String> save(@RequestBody DishDTO dishDTO){
        log.info("请求新增菜品：{}",dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 根据id集合批量删除菜品
     * @param ids 需要删除的菜品的id集合
     * @return 成功结果
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    //前端传参时字符串1，2，3...加上@RequestParam注解转化成集合类型
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("请求批量删除菜品：{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id 查询的菜品的id
     * @return 返回菜品vo
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("请求查询id为{}的菜品",id);
        DishVO dishVO=dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId 查询的菜品的分类id
     * @return 返回菜品vo集合
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("请求根据菜品分类id={}查询菜品",categoryId);
        List<Dish> dishes=dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 分页查询参数
     * @return 返回PageResult结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("请求分页查询菜品：{}",dishPageQueryDTO);
        PageResult pageResult=dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改菜品
     * @param dishDTO 修改的信息
     * @return 成功结果
     */
    @PutMapping
    @ApiOperation("修改菜品")
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    public Result<String> update(@RequestBody DishDTO dishDTO){
        log.info("请求修改菜品：{}",dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售、停售
     * @param status 菜品状态：1为起售，0为停售
     * @return 成功结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品状态修改")
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    public Result<String> status(@PathVariable Integer status,Long id){
        log.info("请求修改id为{}菜品的状态：{}",id,status);
        dishService.status(id,status);
        return Result.success();
    }
}
