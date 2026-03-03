package com.property.service;

import com.property.dto.ChangePasswordRequest;
import com.property.dto.LoginRequest;
import com.property.dto.RegisterRequest;
import com.property.vo.LoginVO;
import com.property.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 登录
     */
    LoginVO login(LoginRequest request);
    
    /**
     * 注册
     */
    void register(RegisterRequest request);
    
    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser();
    
    /**
     * 修改密码
     */
    void changePassword(ChangePasswordRequest request);
}
