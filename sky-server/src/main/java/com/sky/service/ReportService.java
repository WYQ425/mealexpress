package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    /**
     * 根据时间区间统计营业额
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回VO
     */
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    /**
     * 用户数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return VO
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return VO
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量排名统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return VO
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出最近30天的运营数据报表
     * @param response 响应数据
     */
    void exportBusinessData(HttpServletResponse response);
}
