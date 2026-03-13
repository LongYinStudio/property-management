package com.property.vo;

import lombok.Data;

/**
 * 清洁任务返回VO
 */
@Data
public class CleaningVO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String location;
    
    private String description;
    
    private String images;
    
    private Integer status;
    
    private Long cleanerId;
    
    private String cleanerName;
    
    private String cleanResult;
    
    private String cleanTime;
    
    private String createTime;
}
