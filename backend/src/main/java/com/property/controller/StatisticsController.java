package com.property.controller;

import com.property.common.Result;
import com.property.service.StatisticsService;
import com.property.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计管理
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 获取首页统计数据
     */
    @GetMapping("/dashboard")
    public Result<StatisticsVO.DashboardStats> getDashboardStats() {
        return Result.success(statisticsService.getDashboardStats());
    }

    /**
     * 获取业务总览数据
     */
    @GetMapping("/business-overview")
    public Result<StatisticsVO.BusinessOverview> getBusinessOverview() {
        return Result.success(statisticsService.getBusinessOverview());
    }

    /**
     * 获取费用类型统计
     */
    @GetMapping("/fee-type")
    public Result<List<StatisticsVO.FeeTypeStats>> getFeeTypeStats(
            @RequestParam(required = false) Integer year) {
        return Result.success(statisticsService.getFeeTypeStats(year));
    }
    
    /**
     * 获取报修类型统计
     */
    @GetMapping("/repair-type")
    public Result<List<StatisticsVO.RepairTypeStats>> getRepairTypeStats() {
        return Result.success(statisticsService.getRepairTypeStats());
    }
    
    /**
     * 获取月度收入统计
     */
    @GetMapping("/monthly-income")
    public Result<List<StatisticsVO.MonthlyIncome>> getMonthlyIncome(
            @RequestParam(required = false) Integer year) {
        return Result.success(statisticsService.getMonthlyIncome(year));
    }
    
    /**
     * 获取报修状态统计
     */
    @GetMapping("/repair-status")
    public Result<List<StatisticsVO.RepairStatusStats>> getRepairStatusStats() {
        return Result.success(statisticsService.getRepairStatusStats());
    }
    
    /**
     * 获取投诉类型统计
     */
    @GetMapping("/complaint-type")
    public Result<List<StatisticsVO.ComplaintTypeStats>> getComplaintTypeStats() {
        return Result.success(statisticsService.getComplaintTypeStats());
    }
    
    /**
     * 获取清洁任务统计
     */
    @GetMapping("/cleaning")
    public Result<List<StatisticsVO.CleaningStats>> getCleaningStats() {
        return Result.success(statisticsService.getCleaningStats());
    }

    /**
     * 获取访客状态统计
     */
    @GetMapping("/visitor-status")
    public Result<List<StatisticsVO.CategoryStats>> getVisitorStatusStats() {
        return Result.success(statisticsService.getVisitorStatusStats());
    }

    /**
     * 获取车位状态统计
     */
    @GetMapping("/parking-space-status")
    public Result<List<StatisticsVO.CategoryStats>> getParkingSpaceStatusStats() {
        return Result.success(statisticsService.getParkingSpaceStatusStats());
    }

    /**
     * 获取车位租赁状态统计
     */
    @GetMapping("/parking-rental-status")
    public Result<List<StatisticsVO.CategoryStats>> getParkingRentalStatusStats() {
        return Result.success(statisticsService.getParkingRentalStatusStats());
    }

    /**
     * 获取设备巡检状态统计
     */
    @GetMapping("/inspection-status")
    public Result<List<StatisticsVO.CategoryStats>> getInspectionStatusStats() {
        return Result.success(statisticsService.getInspectionStatusStats());
    }

    /**
     * 获取合同状态统计
     */
    @GetMapping("/contract-status")
    public Result<List<StatisticsVO.CategoryStats>> getContractStatusStats() {
        return Result.success(statisticsService.getContractStatusStats());
    }

    /**
     * 获取投票活动类型统计
     */
    @GetMapping("/vote-type")
    public Result<List<StatisticsVO.CategoryStats>> getVoteTypeStats() {
        return Result.success(statisticsService.getVoteTypeStats());
    }

    /**
     * 获取公告类型统计
     */
    @GetMapping("/notice-type")
    public Result<List<StatisticsVO.CategoryStats>> getNoticeTypeStats() {
        return Result.success(statisticsService.getNoticeTypeStats());
    }
}
