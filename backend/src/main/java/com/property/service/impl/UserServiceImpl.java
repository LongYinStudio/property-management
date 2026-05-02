package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.UserRequest;
import com.property.entity.User;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.UserService;
import com.property.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public UserVO create(UserRequest request) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(StringUtils.hasText(request.getPassword()) ? request.getPassword() : "123456"));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus() != null ? request.getStatus() : User.STATUS_ENABLE);
        user.setCommunityId(request.getCommunityId());
        user.setBuildingId(request.getBuildingId());
        user.setRoomId(request.getRoomId());
        user.setDeleted(0);
        
        userMapper.insert(user);
        
        return convertToVO(user);
    }
    
    @Override
    public UserVO update(Long id, UserRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(request.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, request.getUsername());
            if (userMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(request.getUsername());
        }
        
        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        user.setRole(request.getRole());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setCommunityId(request.getCommunityId());
        user.setBuildingId(request.getBuildingId());
        user.setRoomId(request.getRoomId());
        
        userMapper.updateById(user);
        
        return convertToVO(user);
    }
    
    @Override
    public Page<UserVO> getPage(Integer pageNum, Integer pageSize, String username, Integer role) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (role != null) {
            queryWrapper.eq(User::getRole, role);
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }
    
    @Override
    public void delete(Long id) {
        // 不能删除自己
        Long currentUserId = getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new BusinessException("不能删除自己的账号");
        }
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不能删除管理员账号
        if (user.getRole().equals(User.ROLE_ADMIN)) {
            throw new BusinessException("不能删除管理员账号");
        }
        
        userMapper.deleteById(id);
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        // 不能修改自己的状态
        Long currentUserId = getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new BusinessException("不能修改自己的状态");
        }
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不能修改管理员状态
        if (user.getRole().equals(User.ROLE_ADMIN)) {
            throw new BusinessException("不能修改管理员状态");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
    }
    
    @Override
    public List<UserVO> getOwnerList() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, User.ROLE_OWNER);
        queryWrapper.eq(User::getStatus, User.STATUS_ENABLE);
        queryWrapper.orderByAsc(User::getRealName);
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<UserVO> getStaffList() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, User.ROLE_STAFF);
        queryWrapper.eq(User::getStatus, User.STATUS_ENABLE);
        queryWrapper.orderByAsc(User::getRealName);

        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::convertToVO).toList();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
    
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(FORMATTER));
        }
        return vo;
    }
}
