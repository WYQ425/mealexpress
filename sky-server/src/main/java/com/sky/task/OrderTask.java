package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 自定义定时任务，实现订单状态定时处理
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")//每隔一分钟执行
    public void processTimeoutOrder(){
        log.info("处理支付超时订单：{}", new Date());

        //当前时间-15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-1);//正常是15

        //查询未支付状态和下单时间<time的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);
        //如果有超时订单，将状态改为取消
        if(ordersList!=null && !ordersList.isEmpty()){
            ordersList.forEach(orders -> {
                /*orders.setStatus(Orders.CANCELLED);//修改状态为取消
                orders.setCancelReason("支付超市，自动取消");
                orders.setCancelTime(LocalDateTime.now());*/
                orders.setStatus(Orders.TO_BE_CONFIRMED);//因为未有支付功能，修改状态为2待接单
                orders.setCheckoutTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每隔1小时执行
    public void processDeliveryOrder(){
        log.info("处理派送中订单：{}", new Date());

        // select * from orders where status =派送中 4 and order_time < 当前时间-1小时
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if(ordersList != null && !ordersList.isEmpty()){
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}
