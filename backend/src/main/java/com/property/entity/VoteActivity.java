package com.property.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票问卷活动实体
 */
@Data
@TableName("vote_activity")
public class VoteActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    /**
     * 类型：1-业主投票 2-意见征集
     */
    private Integer type;

    /**
     * 状态：1-进行中 2-已结束
     */
    private Integer status;

    /**
     * 投票选项，JSON 数组，仅投票类型使用
     */
    private String options;

    private Long publisherId;

    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public static final int TYPE_VOTE = 1;
    public static final int TYPE_SURVEY = 2;

    public static final int STATUS_OPEN = 1;
    public static final int STATUS_CLOSED = 2;
}
