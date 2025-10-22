package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 新增订单明细数据
     * @param orderDetails 用户提交的订单明细数据
     */
    void insert(ArrayList<OrderDetail> orderDetails);

    /**
     * 根据订单id查询订单明细
     * @param orderId 订单id
     * @return 订单明细集合
     */
    List<OrderDetail> getByOrderId(Long orderId);

}
