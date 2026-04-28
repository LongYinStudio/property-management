package com.property.vo;

import lombok.Data;

import java.util.List;

/**
 * 投票问卷活动 VO
 */
@Data
public class VoteActivityVO {

    private Long id;

    private String title;

    private String description;

    private Integer type;

    private Integer status;

    private List<String> options;

    private Long publisherId;

    private String publisherName;

    private String endTime;

    private String createTime;

    private Boolean participated;

    private Long responseCount;

    private String mySelectedOption;

    private String myContent;

    private List<VoteOptionStatVO> optionStats;

    private List<VoteResponseVO> responses;
}
