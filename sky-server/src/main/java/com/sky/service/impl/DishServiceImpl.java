package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.StatusChangeFailedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO 前端传递的新增的菜品信息
     */
    @Override
    //设计dish表和dish_flavor双表操作，需要添加事务管理并配置发生任何异常时回滚事务
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dishDTO) {
        //新建Entity并进行属性复制
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //状态属性默认起售中
        dish.setStatus(StatusConstant.ENABLE);

        //操作Dish表添加基本信息，并获取返回的自增主键值（通过xml文件：useGeneratedKeys="true" keyProperty="id(主键字段名)"）
        dishMapper.insert(dish);
        Long dishId = dish.getId();

        //获取口味集合并赋值dishId添加到口味表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && !flavors.isEmpty() && !flavors.get(0).getName().isEmpty()) {
            flavors.forEach(f -> f.setDishId(dishId));
            dishFlavorMapper.insertForBatch(flavors);
        }
    }

    /**
     * 根据id集合删除菜品
     * 删除条件：
     * - 起售中的菜品不能删除
     * - 被套餐关联的菜品不能删除
     * - 删除菜品后，关联的口味数据也需要删除掉
     * @param ids id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        //查询菜品表返回菜品集合-->是否存在起售中菜品
        List<Dish> dishes=dishMapper.getByIdForBatch(ids);
        dishes.forEach(d -> {
            if(d.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }});

        //分组查询查询菜品套餐关联表，查看是否存在关联套餐
        Integer count=setMealDishMapper.countByDishIdForBatch(ids);
        if(count>0) throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        //先批量删除口味集合
        dishFlavorMapper.deleteByIdForBatch(ids);

        //再批量删除菜品
        dishMapper.deleteByIdForBatch(ids);
    }

    /**
     * 根据id查询菜品基本信息和口味信息
     * @param id 菜品id
     * @return 返回VO
     */
    @Override
    public DishVO getById(Long id) {
        //获取菜品基本信息
        DishVO dishVO=dishMapper.getById(id);

        //获取口味集合
        List<DishFlavor> dishFlavors=dishFlavorMapper.getByDishId(id);

        //组装VO返回
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 根据分类id查询菜品基本信息
     *
     * @param categoryId 分类id
     * @return 返回起售中的Dish集合
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        //设置Entity
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.getByCategoryId(dish);
    }

    /**
     * 根据分页查询参数进行菜品分页查询
     * @param dishPageQueryDTO 分页查询参数
     * @return PageResult
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.page(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 修改菜品基本信息和口味数据
     * @param dishDTO 修改的信息
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        //创建entity并复制属性
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品数据
        dishMapper.update(dish);

        //先删除后修改菜品数据
        Long dishId = dish.getId();
        dishFlavorMapper.deleteByDishId(dishId);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(f->f.setDishId(dishId));
            dishFlavorMapper.insertForBatch(flavors);
        }
    }

    /**
     * 修改菜品状态
     * @param id 修改菜品的id
     * @param status 修改后的状态
     */
    @Override
    public void status(Long id, Integer status) {
        //如果包含于某一个套餐中，改套餐启用时不能将其禁用
        if(status.equals(StatusConstant.DISABLE)){
            List<Setmeal> setMeals=setMealDishMapper.getsmlByDishId(id);
            setMeals.forEach(s -> {
                if(s.getStatus().equals(StatusConstant.ENABLE)){
                    throw new StatusChangeFailedException(MessageConstant.DISH_DISABLE_FAILED);
                }});
        }
        Dish dish =Dish.builder().id(id).status(status).build();
        dishMapper.update(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
