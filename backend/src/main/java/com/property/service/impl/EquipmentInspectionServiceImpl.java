package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.EquipmentInspectionRequest;
import com.property.entity.EquipmentInspection;
import com.property.entity.User;
import com.property.mapper.EquipmentInspectionMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.EquipmentInspectionService;
import com.property.vo.EquipmentInspectionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 设备巡检服务实现
 */
@Service
@RequiredArgsConstructor
public class EquipmentInspectionServiceImpl implements EquipmentInspectionService {

    private final EquipmentInspectionMapper equipmentInspectionMapper;
    private final UserMapper userMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EquipmentInspectionVO create(EquipmentInspectionRequest request) {
        Long userId = getCurrentUserId();

        EquipmentInspection inspection = new EquipmentInspection();
        inspection.setUserId(userId);
        inspection.setEquipmentName(request.getEquipmentName());
        inspection.setEquipmentType(request.getEquipmentType());
        inspection.setLocation(request.getLocation());
        inspection.setInspectionDate(request.getInspectionDate());
        inspection.setNextInspectionDate(request.getNextInspectionDate());
        inspection.setStatus(request.getStatus());
        inspection.setResult(request.getResult());
        inspection.setIssueDescription(request.getIssueDescription());
        inspection.setImages(request.getImages());
        inspection.setRemark(request.getRemark());
        inspection.setDeleted(0);

        equipmentInspectionMapper.insert(inspection);

        return convertToVO(inspection);
    }

    @Override
    public Page<EquipmentInspectionVO> getPage(Integer pageNum, Integer pageSize, Integer status, Integer equipmentType) {
        Page<EquipmentInspection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EquipmentInspection> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(EquipmentInspection::getStatus, status);
        }
        if (equipmentType != null) {
            queryWrapper.eq(EquipmentInspection::getEquipmentType, equipmentType);
        }
        queryWrapper.orderByDesc(EquipmentInspection::getInspectionDate)
                .orderByDesc(EquipmentInspection::getCreateTime);

        Page<EquipmentInspection> inspectionPage = equipmentInspectionMapper.selectPage(page, queryWrapper);

        Page<EquipmentInspectionVO> voPage = new Page<>(inspectionPage.getCurrent(), inspectionPage.getSize(), inspectionPage.getTotal());
        voPage.setRecords(inspectionPage.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public EquipmentInspectionVO getById(Long id) {
        EquipmentInspection inspection = equipmentInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException("设备巡检记录不存在");
        }

        return convertToVO(inspection);
    }

    @Override
    public void delete(Long id) {
        EquipmentInspection inspection = equipmentInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException("设备巡检记录不存在");
        }

        equipmentInspectionMapper.deleteById(id);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }

    private EquipmentInspectionVO convertToVO(EquipmentInspection inspection) {
        EquipmentInspectionVO vo = new EquipmentInspectionVO();
        BeanUtils.copyProperties(inspection, vo);

        User user = userMapper.selectById(inspection.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }

        if (inspection.getInspectionDate() != null) {
            vo.setInspectionDate(inspection.getInspectionDate().format(DATE_FORMATTER));
        }
        if (inspection.getNextInspectionDate() != null) {
            vo.setNextInspectionDate(inspection.getNextInspectionDate().format(DATE_FORMATTER));
        }
        if (inspection.getCreateTime() != null) {
            vo.setCreateTime(inspection.getCreateTime().format(DATE_TIME_FORMATTER));
        }

        return vo;
    }
}
