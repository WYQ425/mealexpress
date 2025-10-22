package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单数据并返回主键
     * @param orders 用户提交的订单数据
     */
    void insert(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber 订单号
     * @param userId 用户id
     */
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders 修改的具体数据
     */
    void update(Orders orders);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO 分页查询参数
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id 订单主键id
     * @return 返回Entity
     */
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     * @param status 订单状态
     */
    Integer countStatus(Integer status);

    /**
     * 根据状态和下单时间查询订单
     * @param status 订单状态
     * @param orderTime 下单时间
     */
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据map参数统计营业额
     * @param map 包含状态和时间参数
     * @return 返回统计营业额
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map 动态添加（开始时间、结束时间、状态）
     * @return 返回统计数量
     */
    Integer countByMap(Map map);

    /**
     * 查询商品销量排名
     * @param begin 开始时间
     * @param end 结束时间
     * @return 商品集合
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}