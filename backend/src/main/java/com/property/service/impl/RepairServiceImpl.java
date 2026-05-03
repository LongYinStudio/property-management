package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.RepairRequest;
import com.property.entity.Repair;
import com.property.entity.User;
import com.property.mapper.RepairMapper;
import com.property.mapper.UserMapper;
import com.property.security.SecurityUtils;
import com.property.service.RepairService;
import com.property.vo.RepairVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
        Long userId = SecurityUtils.getCurrentUserId();
        
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
        User currentUser = SecurityUtils.getCurrentUser();
        Page<Repair> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Repair> queryWrapper = new LambdaQueryWrapper<>();
        if (!SecurityUtils.isManager(currentUser)) {
            queryWrapper.eq(Repair::getUserId, currentUser.getId());
        }
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
        validateAccess(SecurityUtils.getCurrentUser(), repair.getUserId(), "无权查看该报修记录");

        return convertToVO(repair);
    }
    
    @Override
    public void delete(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        validateAccess(SecurityUtils.getCurrentUser(), repair.getUserId(), "无权删除该报修记录");

        repairMapper.deleteById(id);
    }

    @Override
    public RepairVO assign(Long id, Long handlerId) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (repair.getStatus() != Repair.STATUS_PENDING) {
            throw new BusinessException("只能指派待处理的报修");
        }

        User handler = userMapper.selectById(handlerId);
        if (handler == null) {
            throw new BusinessException("维修人员不存在");
        }

        repair.setHandlerId(handlerId);
        repair.setStatus(Repair.STATUS_PROCESSING);
        repairMapper.updateById(repair);

        return convertToVO(repair);
    }

    @Override
    public RepairVO complete(Long id, String handleResult) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (repair.getStatus() == Repair.STATUS_COMPLETED || repair.getStatus() == Repair.STATUS_CLOSED) {
            throw new BusinessException("该报修已完成或已关闭");
        }

        repair.setStatus(Repair.STATUS_COMPLETED);
        repair.setHandleResult(handleResult);
        repair.setHandleTime(LocalDateTime.now());
        repairMapper.updateById(repair);

        return convertToVO(repair);
    }

    private void validateAccess(User currentUser, Long ownerId, String message) {
        if (SecurityUtils.isManager(currentUser)) {
            return;
        }
        if (!Objects.equals(ownerId, currentUser.getId())) {
            throw new AccessDeniedException(message);
        }
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
