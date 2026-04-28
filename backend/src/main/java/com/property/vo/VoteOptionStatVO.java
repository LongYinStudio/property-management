package com.property.vo;

import lombok.Data;

/**
 * 投票选项统计 VO
 */
@Data
public class VoteOptionStatVO {

    private String optionName;

    private Long count;

    private Double percentage;
}
