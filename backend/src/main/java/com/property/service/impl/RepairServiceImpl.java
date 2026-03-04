package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.RepairRequest;
import com.property.entity.Repair;
import com.property.entity.User;
import com.property.mapper.RepairMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.RepairService;
import com.property.vo.RepairVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 报修服务实现
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl implements RepairService {
    
    private final RepairMapper repairMapper;
    private final UserMapper userMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public RepairVO create(RepairRequest request) {
        Long userId = getCurrentUserId();
        
        Repair repair = new Repair();
        repair.setUserId(userId);
        repair.setTitle(request.getTitle());
        repair.setContent(request.getContent());
        repair.setImages(request.getImages());
        repair.setType(request.getType());
        repair.setStatus(Repair.STATUS_PENDING);
        repair.setDeleted(0);
        
        repairMapper.insert(repair);
        
        return convertToVO(repair);
    }
    
    @Override
    public Page<RepairVO> getPage(Integer pageNum, Integer pageSize, Integer status) {
        Long userId = getCurrentUserId();
        
        Page<Repair> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Repair> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Repair::getUserId, userId);
        if (status != null) {
            queryWrapper.eq(Repair::getStatus, status);
        }
        queryWrapper.orderByDesc(Repair::getCreateTime);
        
        Page<Repair> repairPage = repairMapper.selectPage(page, queryWrapper);
        
        Page<RepairVO> voPage = new Page<>(repairPage.getCurrent(), repairPage.getSize(), repairPage.getTotal());
        voPage.setRecords(repairPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public RepairVO getById(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        // 验证是否是自己的报修
        Long userId = getCurrentUserId();
        if (!repair.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该报修记录");
        }
        
        return convertToVO(repair);
    }
    
    @Override
    public void delete(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        // 验证是否是自己的报修
        Long userId = getCurrentUserId();
        if (!repair.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该报修记录");
        }
        
        repairMapper.deleteById(id);
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
    
    private RepairVO convertToVO(Repair repair) {
        RepairVO vo = new RepairVO();
        BeanUtils.copyProperties(repair, vo);
        
        // 设置用户名
        User user = userMapper.selectById(repair.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }
        
        // 设置处理人名称
        if (repair.getHandlerId() != null) {
            User handler = userMapper.selectById(repair.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getRealName());
            }
        }
        
        // 格式化时间
        if (repair.getCreateTime() != null) {
            vo.setCreateTime(repair.getCreateTime().format(FORMATTER));
        }
        if (repair.getHandleTime() != null) {
            vo.setHandleTime(repair.getHandleTime().format(FORMATTER));
        }
        
        return vo;
    }
}
