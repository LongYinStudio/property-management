package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
}
