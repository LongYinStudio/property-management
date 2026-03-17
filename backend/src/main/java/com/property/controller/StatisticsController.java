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
}
