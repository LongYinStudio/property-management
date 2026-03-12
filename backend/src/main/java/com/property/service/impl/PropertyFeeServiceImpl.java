package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.PropertyFeeRequest;
import com.property.entity.PropertyFee;
import com.property.entity.User;
import com.property.mapper.PropertyFeeMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.PropertyFeeService;
import com.property.vo.PropertyFeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 物业费服务实现
 */
@Service
@RequiredArgsConstructor
public class PropertyFeeServiceImpl implements PropertyFeeService {
    
    private final PropertyFeeMapper propertyFeeMapper;
    private final UserMapper userMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public PropertyFeeVO create(PropertyFeeRequest request) {
        // 验证用户是否存在
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setUserId(request.getUserId());
        propertyFee.setRoomId(request.getRoomId());
        propertyFee.setYear(request.getYear());
        propertyFee.setMonth(request.getMonth());
        propertyFee.setAmount(request.getAmount());
        propertyFee.setType(request.getType());
        propertyFee.setDescription(request.getDescription());
        propertyFee.setStatus(PropertyFee.STATUS_UNPAID);
        propertyFee.setDeleted(0);
        
        propertyFeeMapper.insert(propertyFee);
        
        return convertToVO(propertyFee);
    }
    
    @Override
    public Page<PropertyFeeVO> getPage(Integer pageNum, Integer pageSize, Integer status, Integer type, Integer year) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        
        Page<PropertyFee> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PropertyFee> queryWrapper = new LambdaQueryWrapper<>();
        
        // 业主只能查看自己的物业费
        if (currentUser.getRole() == User.ROLE_OWNER) {
            queryWrapper.eq(PropertyFee::getUserId, currentUser.getId());
        }
        
        if (status != null) {
            queryWrapper.eq(PropertyFee::getStatus, status);
        }
        if (type != null) {
            queryWrapper.eq(PropertyFee::getType, type);
        }
        if (year != null) {
            queryWrapper.eq(PropertyFee::getYear, year);
        }
        queryWrapper.orderByDesc(PropertyFee::getCreateTime);
        
        Page<PropertyFee> feePage = propertyFeeMapper.selectPage(page, queryWrapper);
        
        Page<PropertyFeeVO> voPage = new Page<>(feePage.getCurrent(), feePage.getSize(), feePage.getTotal());
        voPage.setRecords(feePage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public PropertyFeeVO getById(Long id) {
        PropertyFee propertyFee = propertyFeeMapper.selectById(id);
        if (propertyFee == null) {
            throw new BusinessException("物业费记录不存在");
        }
        
        // 验证权限：业主只能查看自己的物业费
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        if (currentUser.getRole() == User.ROLE_OWNER && !propertyFee.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("无权查看该物业费记录");
        }
        
        return convertToVO(propertyFee);
    }
    
    @Override
    public void pay(Long id) {
        PropertyFee propertyFee = propertyFeeMapper.selectById(id);
        if (propertyFee == null) {
            throw new BusinessException("物业费记录不存在");
        }
        
        if (propertyFee.getStatus() == PropertyFee.STATUS_PAID) {
            throw new BusinessException("该物业费已支付");
        }
        
        // 验证权限：业主只能支付自己的物业费
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        if (currentUser.getRole() == User.ROLE_OWNER && !propertyFee.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("无权支付该物业费");
        }
        
        propertyFee.setStatus(PropertyFee.STATUS_PAID);
        propertyFee.setPayTime(LocalDateTime.now());
        propertyFeeMapper.updateById(propertyFee);
    }
    
    @Override
    public void delete(Long id) {
        PropertyFee propertyFee = propertyFeeMapper.selectById(id);
        if (propertyFee == null) {
            throw new BusinessException("物业费记录不存在");
        }
        
        propertyFeeMapper.deleteById(id);
    }
    
    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return (LoginUser) authentication.getPrincipal();
    }
    
    private PropertyFeeVO convertToVO(PropertyFee propertyFee) {
        PropertyFeeVO vo = new PropertyFeeVO();
        BeanUtils.copyProperties(propertyFee, vo);
        
        // 设置用户名
        User user = userMapper.selectById(propertyFee.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }
        
        // 格式化时间
        if (propertyFee.getCreateTime() != null) {
            vo.setCreateTime(propertyFee.getCreateTime().format(FORMATTER));
        }
        if (propertyFee.getPayTime() != null) {
            vo.setPayTime(propertyFee.getPayTime().format(FORMATTER));
        }
        
        return vo;
    }
}
