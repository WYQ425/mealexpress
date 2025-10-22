package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.StatusChangeFailedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增套餐
     * 包括套餐基本信息和套餐菜品关系表
     * @param setmealDTO 新增的套餐信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)//多表操作，添加事务
    public void save(SetmealDTO setmealDTO) {
        //创建Entity并进行属性赋值
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //新增套餐默认未起售
        setmeal.setStatus(StatusConstant.DISABLE);

        //添加到数据库并返回主键
        setMealMapper.insert(setmeal);
        Long setMealId = setmeal.getId();

        //往关系表中赋值返回的套餐id随后添加到数据库中
        List<SetmealDish> setMealDishes = setmealDTO.getSetmealDishes();
        if(setMealDishes!=null&&!setMealDishes.isEmpty()){
            setMealDishes.forEach(sd->sd.setSetmealId(setMealId));
            setMealDishMapper.insertForBatch(setMealDishes);
        }
    }

    /**
     * 修改套餐的状态
     * @param id 修改的套餐的id
     * @param status 套餐状态，1表示起售，0表示停售
     */
    @Override
    public void status(Long id, Integer status) {
        //如果是启用套餐，先查看是否包含未启用菜品
        if(status.equals(StatusConstant.ENABLE)){
            //获取套餐包含的菜品集合
            List<Dish> dishes=setMealDishMapper.getDishes(id);
            dishes.forEach(dish ->{
                //如果包含未起售菜品，则无法起售套餐
                if(dish.getStatus().equals(StatusConstant.DISABLE)) {
                    throw new StatusChangeFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }});
        }
        //创建Entity后调用update
        Setmeal setmeal=Setmeal.builder().id(id).status(status).build();
        setMealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * 起售中的套餐不能删除
     * @param ids 批量删除套餐的id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)//多表操作，添加事务
    public void deleteBatch(List<Long> ids) {
        //获取套餐集合，查看是否包含起售中套餐
        List<Setmeal> setMeals=setMealMapper.getByIds(ids);
        setMeals.forEach(sm->{
            if (sm.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }});
        //不包含起售套餐，可以删除,先删除套餐基本信息的，再删除套餐-菜品关系表中的
        setMealMapper.deleteBatch(ids);
        setMealDishMapper.deleteBatch(ids);
    }

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO 分页查询参数
     * @return pageResult结结果
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page=setMealMapper.page(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 通过套餐id获取套餐VO信息
     * @param id 套餐id
     * @return VO
     */
    @Override
    public SetmealVO getById(Long id) {
        //先找套餐表和菜品分类表返回vo
        SetmealVO setmealVO=setMealMapper.getById(id);

        //再找套餐-菜品关系表返回关系集合
        List<SetmealDish> setMealDishes=setMealDishMapper.getBySid(id);

        //属性赋值成完整VO
        setmealVO.setSetmealDishes(setMealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐信息
     * @param setmealDTO 修改的套餐具体信息
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        //修改基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.update(setmeal);

        //先删除原来的套餐-菜品关系
        Long setMealId = setmealDTO.getId();
        setMealDishMapper.deleteBySid(setMealId);

        //如果修改后的关系集合不为空就添加
        List<SetmealDish> setMealDishes = setmealDTO.getSetmealDishes();
        if(setMealDishes!=null&&!setMealDishes.isEmpty()){
            setMealDishes.forEach(sd->sd.setSetmealId(setMealId));
            setMealDishMapper.insertForBatch(setMealDishes);
        }
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }
}
