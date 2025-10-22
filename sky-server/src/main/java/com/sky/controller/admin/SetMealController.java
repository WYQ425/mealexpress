package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    /**
     * 新增套餐
     * @param setmealDTO 套餐信息
     * @return 成功结果
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setMealCache",key="#setmealDTO.categoryId")
    /*
     * @CacheEvict作用: 清理指定缓存
     *value: 缓存的名称，每个缓存名称下面可以有多个key
     *key: 缓存的key  ----------> 支持Spring的表达式语言SPEL语法
     */
    public Result<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("请求新增套餐：{}",setmealDTO);
        setMealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 修改套餐状态
     * @param status 套餐状态，1表示起售，0表示停售
     * @param id 套餐id
     * @return 成功结果
     */
    @PostMapping("//status/{status}")
    @ApiOperation("修改套餐状态")
    @CacheEvict(cacheNames = "setMealCache",allEntries = true)
    public Result<String> status(@PathVariable Integer status, Long id){
        log.info("请求修改id为{}的套餐的状态为：{}",id,status);
        setMealService.status(id,status);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids 删除的套餐id集合
     * @return 成功结果
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setMealCache",allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("请求批量删除套餐，ids：{}",ids);
        setMealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 分页查询套餐,管理端不用缓存，因为不会出现高访问
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("请求分页查询套餐，分页参数：{}",setmealPageQueryDTO);
        PageResult pageResult=setMealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询套餐
     * @param id 查询的套餐id
     * @return 返回套餐VO
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐：{}",id);
        SetmealVO setmealVO =setMealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐数据
     * @param setmealDTO 修改的套餐信息
     * @return 成功结果
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setMealCache",allEntries = true)
    public Result<String> update(@RequestBody SetmealDTO setmealDTO){
        log.info("请求修改套餐信息：{}",setmealDTO);
        setMealService.update(setmealDTO);
        return Result.success();
    }
}
