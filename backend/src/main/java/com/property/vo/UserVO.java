package com.property.vo;

import lombok.Data;

/**
 * 用户信息VO
 */
@Data
public class UserVO {
    
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer role;
    private String roleName;
    private Integer status;
    private Long communityId;
    private String communityName;
    private Long buildingId;
    private String buildingName;
    private Long roomId;
    private String roomName;
    private String createTime;
    
    public String getRoleName() {
        return switch (role) {
            case 1 -> "管理员";
            case 2 -> "物业人员";
            case 3 -> "业主";
            default -> "未知";
        };
    }
}
