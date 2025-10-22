package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO 提交的订单详情
     * @return 返回订单支付数据
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO 支付参数
     * @return 返回前端支付相关数据
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 订单状态
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param page 页码
     * @param pageSize 每页记录数
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return 返回page结果
     */
    PageResult pageQuery(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 返回订单VO
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     * @param id 订单id
     */
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     * @param id 订单id
     */
    void repetition(Long id);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO 查询条件
     * @return 返回分页查询的结果
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return 封装好的订单数量统计VO
     */
    OrderStatisticsVO statistics();

    /**
     * 完成接单功能
     * @param ordersConfirmDTO 订单DTO包含id
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO 包含订单id的DTO
     * @throws Exception 抛出所有异常
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 取消订单
     * @param ordersCancelDTO 包含订单id的DTO
     * @throws Exception 抛出所有异常
     */
    void cancel(OrdersCancelDTO ordersCancelDTO)throws Exception;

    /**
     * 派送订单
     * @param id 订单id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id 订单id
     */
    void complete(Long id);

    /**
     * 用户催单
     * @param id 订单id
     */
    void reminder(Long id);
}
