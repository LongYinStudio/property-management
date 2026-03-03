package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.common.BusinessException;
import com.property.common.ResultCode;
import com.property.dto.ChangePasswordRequest;
import com.property.dto.LoginRequest;
import com.property.dto.RegisterRequest;
import com.property.dto.UpdateProfileRequest;
import com.property.entity.User;
import com.property.mapper.UserMapper;
import com.property.security.JwtUtils;
import com.property.security.LoginUser;
import com.property.service.AuthService;
import com.property.vo.LoginVO;
import com.property.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public LoginVO login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = loginUser.getUser();
        
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(convertToUserVO(user));
        
        return loginVO;
    }
    
    @Override
    public void register(RegisterRequest request) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoomId(request.getRoomId());
        user.setRole(User.ROLE_OWNER);  // 默认为业主角色
        user.setStatus(User.STATUS_ENABLE);
        user.setDeleted(0);
        
        userMapper.insert(user);
    }
    
    @Override
    public UserVO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = userMapper.selectById(loginUser.getUser().getId());
        
        return convertToUserVO(user);
    }
    
    @Override
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = userMapper.selectById(loginUser.getUser().getId());
        
        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }
    
    @Override
    public UserVO updateProfile(UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = userMapper.selectById(loginUser.getUser().getId());
        
        // 更新个人信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        
        userMapper.updateById(user);
        
        return convertToUserVO(user);
    }
    
    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(FORMATTER));
        }
        return vo;
    }
}
