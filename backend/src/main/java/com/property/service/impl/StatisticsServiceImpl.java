package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.entity.*;
import com.property.mapper.*;
import com.property.service.StatisticsService;
import com.property.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final UserMapper userMapper;
    private final RepairMapper repairMapper;
    private final ComplaintMapper complaintMapper;
    private final PropertyFeeMapper propertyFeeMapper;
    private final CleaningMapper cleaningMapper;
    
    @Override
    public StatisticsVO.DashboardStats getDashboardStats() {
        StatisticsVO.DashboardStats stats = new StatisticsVO.DashboardStats();
        
        // 用户总数
        stats.setUserCount(userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, User.STATUS_ENABLE)
        ));
        
        // 待处理报修数
        stats.setPendingRepair(repairMapper.selectCount(
                new LambdaQueryWrapper<Repair>()
                        .eq(Repair::getStatus, Repair.STATUS_PENDING)
        ));
        
        // 待缴费用数
        stats.setUnpaidFee(propertyFeeMapper.selectCount(
                new LambdaQueryWrapper<PropertyFee>()
                        .eq(PropertyFee::getStatus, PropertyFee.STATUS_UNPAID)
        ));
        
        // 待处理投诉数
        stats.setPendingComplaint(complaintMapper.selectCount(
                new LambdaQueryWrapper<Complaint>()
                        .eq(Complaint::getStatus, Complaint.STATUS_PENDING)
        ));
        
        // 待处理清洁任务数
        stats.setPendingCleaning(cleaningMapper.selectCount(
                new LambdaQueryWrapper<Cleaning>()
                        .eq(Cleaning::getStatus, Cleaning.STATUS_PENDING)
        ));
        
        return stats;
    }
    
    @Override
    public List<StatisticsVO.FeeTypeStats> getFeeTypeStats(Integer year) {
        List<StatisticsVO.FeeTypeStats> result = new ArrayList<>();
        int targetYear = year != null ? year : LocalDate.now().getYear();
        
        // 按费用类型统计已支付金额
        for (int type : new int[]{PropertyFee.TYPE_PROPERTY, PropertyFee.TYPE_PARKING, 
                PropertyFee.TYPE_WATER, PropertyFee.TYPE_ELECTRIC}) {
            List<PropertyFee> fees = propertyFeeMapper.selectList(
                    new LambdaQueryWrapper<PropertyFee>()
                            .eq(PropertyFee::getType, type)
                            .eq(PropertyFee::getStatus, PropertyFee.STATUS_PAID)
                            .eq(PropertyFee::getYear, targetYear)
            );
            
            BigDecimal totalAmount = fees.stream()
                    .map(PropertyFee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            StatisticsVO.FeeTypeStats stats = new StatisticsVO.FeeTypeStats();
            stats.setType(type);
            stats.setTypeName(getFeeTypeName(type));
            stats.setAmount(totalAmount);
            result.add(stats);
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO.RepairTypeStats> getRepairTypeStats() {
        List<StatisticsVO.RepairTypeStats> result = new ArrayList<>();
        
        for (int type : new int[]{Repair.TYPE_WATER, Repair.TYPE_ELECTRIC, 
                Repair.TYPE_DOOR, Repair.TYPE_OTHER}) {
            Long count = repairMapper.selectCount(
                    new LambdaQueryWrapper<Repair>()
                            .eq(Repair::getType, type)
            );
            
            StatisticsVO.RepairTypeStats stats = new StatisticsVO.RepairTypeStats();
            stats.setType(type);
            stats.setTypeName(getRepairTypeName(type));
            stats.setCount(count);
            result.add(stats);
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO.MonthlyIncome> getMonthlyIncome(Integer year) {
        List<StatisticsVO.MonthlyIncome> result = new ArrayList<>();
        int targetYear = year != null ? year : LocalDate.now().getYear();
        
        for (int month = 1; month <= 12; month++) {
            List<PropertyFee> fees = propertyFeeMapper.selectList(
                    new LambdaQueryWrapper<PropertyFee>()
                            .eq(PropertyFee::getStatus, PropertyFee.STATUS_PAID)
                            .eq(PropertyFee::getYear, targetYear)
                            .eq(PropertyFee::getMonth, month)
            );
            
            BigDecimal totalAmount = fees.stream()
                    .map(PropertyFee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            StatisticsVO.MonthlyIncome income = new StatisticsVO.MonthlyIncome();
            income.setMonth(month);
            income.setAmount(totalAmount);
            result.add(income);
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO.RepairStatusStats> getRepairStatusStats() {
        List<StatisticsVO.RepairStatusStats> result = new ArrayList<>();
        
        for (int status : new int[]{Repair.STATUS_PENDING, Repair.STATUS_PROCESSING, 
                Repair.STATUS_COMPLETED, Repair.STATUS_CLOSED}) {
            Long count = repairMapper.selectCount(
                    new LambdaQueryWrapper<Repair>()
                            .eq(Repair::getStatus, status)
            );
            
            StatisticsVO.RepairStatusStats stats = new StatisticsVO.RepairStatusStats();
            stats.setStatus(status);
            stats.setStatusName(getRepairStatusName(status));
            stats.setCount(count);
            result.add(stats);
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO.ComplaintTypeStats> getComplaintTypeStats() {
        List<StatisticsVO.ComplaintTypeStats> result = new ArrayList<>();
        
        for (int type : new int[]{Complaint.TYPE_COMPLAINT, Complaint.TYPE_SUGGESTION}) {
            Long count = complaintMapper.selectCount(
                    new LambdaQueryWrapper<Complaint>()
                            .eq(Complaint::getType, type)
            );
            
            StatisticsVO.ComplaintTypeStats stats = new StatisticsVO.ComplaintTypeStats();
            stats.setType(type);
            stats.setTypeName(getComplaintTypeName(type));
            stats.setCount(count);
            result.add(stats);
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO.CleaningStats> getCleaningStats() {
        List<StatisticsVO.CleaningStats> result = new ArrayList<>();
        
        for (int status : new int[]{Cleaning.STATUS_PENDING, Cleaning.STATUS_PROCESSING, 
                Cleaning.STATUS_COMPLETED}) {
            Long count = cleaningMapper.selectCount(
                    new LambdaQueryWrapper<Cleaning>()
                            .eq(Cleaning::getStatus, status)
            );
            
            StatisticsVO.CleaningStats stats = new StatisticsVO.CleaningStats();
            stats.setStatus(status);
            stats.setStatusName(getCleaningStatusName(status));
            stats.setCount(count);
            result.add(stats);
        }
        
        return result;
    }
    
    private String getFeeTypeName(Integer type) {
        return switch (type) {
            case PropertyFee.TYPE_PROPERTY -> "物业费";
            case PropertyFee.TYPE_PARKING -> "停车费";
            case PropertyFee.TYPE_WATER -> "水费";
            case PropertyFee.TYPE_ELECTRIC -> "电费";
            default -> "其他";
        };
    }
    
    private String getRepairTypeName(Integer type) {
        return switch (type) {
            case Repair.TYPE_WATER -> "水管";
            case Repair.TYPE_ELECTRIC -> "电路";
            case Repair.TYPE_DOOR -> "门窗";
            default -> "其他";
        };
    }
    
    private String getRepairStatusName(Integer status) {
        return switch (status) {
            case Repair.STATUS_PENDING -> "待处理";
            case Repair.STATUS_PROCESSING -> "处理中";
            case Repair.STATUS_COMPLETED -> "已完成";
            case Repair.STATUS_CLOSED -> "已关闭";
            default -> "未知";
        };
    }
    
    private String getComplaintTypeName(Integer type) {
        return switch (type) {
            case Complaint.TYPE_COMPLAINT -> "投诉";
            case Complaint.TYPE_SUGGESTION -> "建议";
            default -> "其他";
        };
    }
    
    private String getCleaningStatusName(Integer status) {
        return switch (status) {
            case Cleaning.STATUS_PENDING -> "待处理";
            case Cleaning.STATUS_PROCESSING -> "处理中";
            case Cleaning.STATUS_COMPLETED -> "已完成";
            default -> "未知";
        };
    }
}
