package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.CleaningRequest;
import com.property.entity.Cleaning;
import com.property.entity.User;
import com.property.mapper.CleaningMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.CleaningService;
import com.property.vo.CleaningVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 清洁服务实现
 */
@Service
@RequiredArgsConstructor
public class CleaningServiceImpl implements CleaningService {
    
    private final CleaningMapper cleaningMapper;
    private final UserMapper userMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public CleaningVO create(CleaningRequest request) {
        Long userId = getCurrentUserId();
        
        Cleaning cleaning = new Cleaning();
        cleaning.setUserId(userId);
        cleaning.setLocation(request.getLocation());
        cleaning.setDescription(request.getDescription());
        cleaning.setImages(request.getImages());
        cleaning.setStatus(Cleaning.STATUS_PENDING);
        cleaning.setDeleted(0);
        
        cleaningMapper.insert(cleaning);
        
        return convertToVO(cleaning);
    }
    
    @Override
    public Page<CleaningVO> getPage(Integer pageNum, Integer pageSize, Integer status) {
        Long userId = getCurrentUserId();
        
        Page<Cleaning> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Cleaning> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(Cleaning::getUserId, userId);
        if (status != null) {
            queryWrapper.eq(Cleaning::getStatus, status);
        }
        queryWrapper.orderByDesc(Cleaning::getCreateTime);
        
        Page<Cleaning> cleaningPage = cleaningMapper.selectPage(page, queryWrapper);
        
        Page<CleaningVO> voPage = new Page<>(cleaningPage.getCurrent(), cleaningPage.getSize(), cleaningPage.getTotal());
        voPage.setRecords(cleaningPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public CleaningVO getById(Long id) {
        Cleaning cleaning = cleaningMapper.selectById(id);
        if (cleaning == null) {
            throw new BusinessException("清洁任务不存在");
        }
        
        // 验证是否是自己的清洁任务
        Long userId = getCurrentUserId();
        if (!cleaning.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该清洁任务");
        }
        
        return convertToVO(cleaning);
    }
    
    @Override
    public void delete(Long id) {
        Cleaning cleaning = cleaningMapper.selectById(id);
        if (cleaning == null) {
            throw new BusinessException("清洁任务不存在");
        }
        
        // 验证是否是自己的清洁任务
        Long userId = getCurrentUserId();
        if (!cleaning.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该清洁任务");
        }
        
        cleaningMapper.deleteById(id);
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
    
    private CleaningVO convertToVO(Cleaning cleaning) {
        CleaningVO vo = new CleaningVO();
        BeanUtils.copyProperties(cleaning, vo);
        
        // 设置用户名
        User user = userMapper.selectById(cleaning.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }
        
        // 设置清洁人员名称
        if (cleaning.getCleanerId() != null) {
            User cleaner = userMapper.selectById(cleaning.getCleanerId());
            if (cleaner != null) {
                vo.setCleanerName(cleaner.getRealName());
            }
        }
        
        // 格式化时间
        if (cleaning.getCreateTime() != null) {
            vo.setCreateTime(cleaning.getCreateTime().format(FORMATTER));
        }
        if (cleaning.getCleanTime() != null) {
            vo.setCleanTime(cleaning.getCleanTime().format(FORMATTER));
        }
        
        return vo;
    }
}
