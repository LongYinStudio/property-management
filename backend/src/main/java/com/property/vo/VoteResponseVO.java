package com.property.vo;

import lombok.Data;

/**
 * 参与记录 VO
 */
@Data
public class VoteResponseVO {

    private Long id;

    private Long userId;

    private String userName;

    private String selectedOption;

    private String content;

    private String createTime;
}
