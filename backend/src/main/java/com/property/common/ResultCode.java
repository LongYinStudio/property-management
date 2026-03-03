package com.property.common;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    
    // 业务错误 5xx
    INTERNAL_ERROR(500, "系统内部错误"),
    BUSINESS_ERROR(501, "业务处理失败"),
    
    // 密码相关错误 5xx
    OLD_PASSWORD_ERROR(502, "原密码错误"),
    PASSWORD_NOT_MATCH(503, "两次密码输入不一致");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
