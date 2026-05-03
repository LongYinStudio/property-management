package com.property.vo;

import lombok.Data;

/**
 * 通知公告返回VO
 */
@Data
public class NoticeVO {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    private Integer isTop;

    private Long publisherId;

    private String publisherName;

    private String publishTime;

    private String createTime;

    private String updateTime;
}
