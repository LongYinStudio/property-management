package com.property.service;

import com.property.vo.StatisticsVO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取首页统计数据
     */
    StatisticsVO.DashboardStats getDashboardStats();

    /**
     * 获取业务总览数据
     */
    StatisticsVO.BusinessOverview getBusinessOverview();

    /**
     * 获取费用类型统计
     */
    List<StatisticsVO.FeeTypeStats> getFeeTypeStats(Integer year);

    /**
     * 获取报修类型统计
     */
    List<StatisticsVO.RepairTypeStats> getRepairTypeStats();

    /**
     * 获取月度收入统计
     */
    List<StatisticsVO.MonthlyIncome> getMonthlyIncome(Integer year);

    /**
     * 获取报修状态统计
     */
    List<StatisticsVO.RepairStatusStats> getRepairStatusStats();

    /**
     * 获取投诉类型统计
     */
    List<StatisticsVO.ComplaintTypeStats> getComplaintTypeStats();

    /**
     * 获取清洁任务统计
     */
    List<StatisticsVO.CleaningStats> getCleaningStats();

    /**
     * 获取访客状态统计
     */
    List<StatisticsVO.CategoryStats> getVisitorStatusStats();

    /**
     * 获取车位状态统计
     */
    List<StatisticsVO.CategoryStats> getParkingSpaceStatusStats();

    /**
     * 获取车位租赁状态统计
     */
    List<StatisticsVO.CategoryStats> getParkingRentalStatusStats();

    /**
     * 获取设备巡检状态统计
     */
    List<StatisticsVO.CategoryStats> getInspectionStatusStats();

    /**
     * 获取合同状态统计
     */
    List<StatisticsVO.CategoryStats> getContractStatusStats();

    /**
     * 获取投票活动类型统计
     */
    List<StatisticsVO.CategoryStats> getVoteTypeStats();

    /**
     * 获取公告类型统计
     */
    List<StatisticsVO.CategoryStats> getNoticeTypeStats();
}
