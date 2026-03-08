package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.ComplaintRequest;
import com.property.entity.Complaint;
import com.property.entity.User;
import com.property.mapper.ComplaintMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.ComplaintService;
import com.property.vo.ComplaintVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 投诉建议服务实现
 */
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
    
    private final ComplaintMapper complaintMapper;
    private final UserMapper userMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ComplaintVO create(ComplaintRequest request) {
        Long userId = getCurrentUserId();
        
        Complaint complaint = new Complaint();
        complaint.setUserId(userId);
        complaint.setTitle(request.getTitle());
        complaint.setContent(request.getContent());
        complaint.setType(request.getType());
        complaint.setStatus(Complaint.STATUS_PENDING);
        complaint.setDeleted(0);
        
        complaintMapper.insert(complaint);
        
        return convertToVO(complaint);
    }
    
    @Override
    public Page<ComplaintVO> getPage(Integer pageNum, Integer pageSize, Integer type) {
        Long userId = getCurrentUserId();
        
        Page<Complaint> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Complaint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Complaint::getUserId, userId);
        if (type != null) {
            queryWrapper.eq(Complaint::getType, type);
        }
        queryWrapper.orderByDesc(Complaint::getCreateTime);
        
        Page<Complaint> complaintPage = complaintMapper.selectPage(page, queryWrapper);
        
        Page<ComplaintVO> voPage = new Page<>(complaintPage.getCurrent(), complaintPage.getSize(), complaintPage.getTotal());
        voPage.setRecords(complaintPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public ComplaintVO getById(Long id) {
        Complaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("投诉建议记录不存在");
        }
        
        // 验证是否是自己的投诉建议
        Long userId = getCurrentUserId();
        if (!complaint.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该投诉建议记录");
        }
        
        return convertToVO(complaint);
    }
    
    @Override
    public void delete(Long id) {
        Complaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("投诉建议记录不存在");
        }
        
        // 验证是否是自己的投诉建议
        Long userId = getCurrentUserId();
        if (!complaint.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该投诉建议记录");
        }
        
        complaintMapper.deleteById(id);
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
    
    private ComplaintVO convertToVO(Complaint complaint) {
        ComplaintVO vo = new ComplaintVO();
        BeanUtils.copyProperties(complaint, vo);
        
        // 设置用户名
        User user = userMapper.selectById(complaint.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }
        
        // 设置处理人名称
        if (complaint.getHandlerId() != null) {
            User handler = userMapper.selectById(complaint.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getRealName());
            }
        }
        
        // 格式化时间
        if (complaint.getCreateTime() != null) {
            vo.setCreateTime(complaint.getCreateTime().format(FORMATTER));
        }
        if (complaint.getReplyTime() != null) {
            vo.setReplyTime(complaint.getReplyTime().format(FORMATTER));
        }
        
        return vo;
    }
}
