package com.property.vo;

import lombok.Data;

/**
 * 投诉建议返回VO
 */
@Data
public class ComplaintVO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String title;
    
    private String content;
    
    private Integer type;
    
    private Integer status;
    
    private Long handlerId;
    
    private String handlerName;
    
    private String reply;
    
    private String replyTime;
    
    private String createTime;
}
