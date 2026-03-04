package com.property.vo;

import lombok.Data;

/**
 * 报修返回VO
 */
@Data
public class RepairVO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String title;
    
    private String content;
    
    private String images;
    
    private Integer type;
    
    private Integer status;
    
    private Long handlerId;
    
    private String handlerName;
    
    private String handleResult;
    
    private String handleTime;
    
    private String createTime;
}
