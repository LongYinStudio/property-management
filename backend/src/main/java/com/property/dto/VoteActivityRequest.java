package com.property.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票问卷活动请求 DTO
 */
@Data
public class VoteActivityRequest {

    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题长度不能超过100位")
    private String title;

    @Size(max = 1000, message = "活动说明长度不能超过1000位")
    private String description;

    @NotNull(message = "活动类型不能为空")
    @Min(value = 1, message = "活动类型不正确")
    @Max(value = 2, message = "活动类型不正确")
    private Integer type;

    private LocalDateTime endTime;

    /**
     * 投票选项，仅业主投票使用
     */
    @Size(max = 10, message = "投票选项不能超过10个")
    private List<String> options;

    @AssertTrue(message = "业主投票至少需要两个有效选项")
    public boolean hasEnoughOptionsForVote() {
        if (type == null || type != 1) {
            return true;
        }
        if (options == null) {
            return false;
        }
        long count = options.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .count();
        return count >= 2;
    }
}
