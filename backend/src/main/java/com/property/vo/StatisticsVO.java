package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO {
    
    /**
     * 首页统计数据
     */
    @Data
    public static class DashboardStats {
        /** 用户总数 */
        private Long userCount;
        /** 待处理报修数 */
        private Long pendingRepair;
        /** 待缴费用数 */
        private Long unpaidFee;
        /** 待处理投诉数 */
        private Long pendingComplaint;
        /** 待处理清洁任务数 */
        private Long pendingCleaning;
    }

    /**
     * 业务总览数据
     */
    @Data
    public static class BusinessOverview {
        /** 业主总数 */
        private Long ownerCount;
        /** 车位总数 */
        private Long parkingSpaceCount;
        /** 已支付租赁数 */
        private Long paidParkingRentalCount;
        /** 待通行访客数 */
        private Long pendingVisitorCount;
        /** 异常巡检数 */
        private Long abnormalInspectionCount;
        /** 生效合同数 */
        private Long activeContractCount;
        /** 进行中活动数 */
        private Long openVoteCount;
        /** 已发布公告数 */
        private Long publishedNoticeCount;
    }
    
    /**
     * 费用类型统计
     */
    @Data
    public static class FeeTypeStats {
        private Integer type;
        private String typeName;
        private BigDecimal amount;
    }
    
    /**
     * 报修类型统计
     */
    @Data
    public static class RepairTypeStats {
        private Integer type;
        private String typeName;
        private Long count;
    }
    
    /**
     * 月度收入统计
     */
    @Data
    public static class MonthlyIncome {
        private Integer month;
        private BigDecimal amount;
    }
    
    /**
     * 报修状态统计
     */
    @Data
    public static class RepairStatusStats {
        private Integer status;
        private String statusName;
        private Long count;
    }
    
    /**
     * 投诉类型统计
     */
    @Data
    public static class ComplaintTypeStats {
        private Integer type;
        private String typeName;
        private Long count;
    }
    
    /**
     * 清洁任务统计
     */
    @Data
    public static class CleaningStats {
        private Integer status;
        private String statusName;
        private Long count;
    }

    /**
     * 通用分类统计
     */
    @Data
    public static class CategoryStats {
        private Integer value;
        private String label;
        private Long count;
    }
}
