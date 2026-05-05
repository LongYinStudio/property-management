package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.FacilityRequest;
import com.property.entity.EquipmentInspection;
import com.property.entity.Facility;
import com.property.mapper.EquipmentInspectionMapper;
import com.property.mapper.FacilityMapper;
import com.property.service.FacilityService;
import com.property.vo.FacilityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 设备设施服务实现
 */
@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final FacilityMapper facilityMapper;
    private final EquipmentInspectionMapper equipmentInspectionMapper;

    @Override
    public FacilityVO create(FacilityRequest request) {
        Facility facility = new Facility();
        fillFacility(facility, request);
        facility.setDeleted(0);
        facilityMapper.insert(facility);
        return convertToVO(facility);
    }

    @Override
    public FacilityVO update(Long id, FacilityRequest request) {
        Facility facility = getFacilityOrThrow(id);
        fillFacility(facility, request);
        facilityMapper.updateById(facility);
        return convertToVO(facility);
    }

    @Override
    public Page<FacilityVO> getPage(Integer pageNum, Integer pageSize, String keyword, Integer type, Integer status) {
        Page<Facility> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Facility> queryWrapper = buildQueryWrapper(keyword, type, status);
        Page<Facility> facilityPage = facilityMapper.selectPage(page, queryWrapper);
        Page<FacilityVO> voPage = new Page<>(facilityPage.getCurrent(), facilityPage.getSize(), facilityPage.getTotal());
        voPage.setRecords(facilityPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public FacilityVO getById(Long id) {
        return convertToVO(getFacilityOrThrow(id));
    }

    @Override
    public List<FacilityVO> getList(String keyword, Integer type, Integer status) {
        LambdaQueryWrapper<Facility> queryWrapper = buildQueryWrapper(keyword, type, status);
        return facilityMapper.selectList(queryWrapper).stream().map(this::convertToVO).toList();
    }

    @Override
    public void delete(Long id) {
        Facility facility = getFacilityOrThrow(id);
        Long inspectionCount = equipmentInspectionMapper.selectCount(
                new LambdaQueryWrapper<EquipmentInspection>().eq(EquipmentInspection::getFacilityId, id)
        );
        if (inspectionCount > 0) {
            throw new BusinessException("该设施已有关联巡检记录，无法删除");
        }
        facilityMapper.deleteById(facility.getId());
    }

    private LambdaQueryWrapper<Facility> buildQueryWrapper(String keyword, Integer type, Integer status) {
        LambdaQueryWrapper<Facility> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(Facility::getName, keyword.trim())
                    .or()
                    .like(Facility::getLocation, keyword.trim()));
        }
        if (type != null) {
            queryWrapper.eq(Facility::getType, type);
        }
        if (status != null) {
            queryWrapper.eq(Facility::getStatus, status);
        }
        queryWrapper.orderByAsc(Facility::getType)
                .orderByAsc(Facility::getName)
                .orderByDesc(Facility::getCreateTime);
        return queryWrapper;
    }

    private Facility getFacilityOrThrow(Long id) {
        Facility facility = facilityMapper.selectById(id);
        if (facility == null) {
            throw new BusinessException("设施不存在");
        }
        return facility;
    }

    private void fillFacility(Facility facility, FacilityRequest request) {
        facility.setName(normalizeRequiredText(request.getName(), "设施名称不能为空"));
        facility.setType(request.getType());
        facility.setLocation(normalizeOptionalText(request.getLocation()));
        facility.setStatus(request.getStatus() != null ? request.getStatus() : Facility.STATUS_NORMAL);
        facility.setLastCheckDate(request.getLastCheckDate());
        facility.setNextCheckDate(request.getNextCheckDate());
        facility.setRemark(normalizeOptionalText(request.getRemark()));
    }

    private FacilityVO convertToVO(Facility facility) {
        FacilityVO vo = new FacilityVO();
        BeanUtils.copyProperties(facility, vo);
        if (facility.getLastCheckDate() != null) {
            vo.setLastCheckDate(facility.getLastCheckDate().format(DATE_FORMATTER));
        }
        if (facility.getNextCheckDate() != null) {
            vo.setNextCheckDate(facility.getNextCheckDate().format(DATE_FORMATTER));
        }
        if (facility.getCreateTime() != null) {
            vo.setCreateTime(facility.getCreateTime().format(DATE_TIME_FORMATTER));
        }
        if (facility.getUpdateTime() != null) {
            vo.setUpdateTime(facility.getUpdateTime().format(DATE_TIME_FORMATTER));
        }
        return vo;
    }

    private String normalizeRequiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
