package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.CommunityRequest;
import com.property.entity.Building;
import com.property.entity.Community;
import com.property.entity.User;
import com.property.mapper.BuildingMapper;
import com.property.mapper.CommunityMapper;
import com.property.mapper.UserMapper;
import com.property.service.CommunityService;
import com.property.vo.CommunityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 小区服务实现
 */
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CommunityMapper communityMapper;
    private final UserMapper userMapper;
    private final BuildingMapper buildingMapper;

    @Override
    public CommunityVO create(CommunityRequest request) {
        String name = normalizeRequiredText(request.getName());
        validateDuplicateName(name, null);

        Community community = new Community();
        fillCommunity(community, request, name);
        community.setDeleted(0);
        communityMapper.insert(community);
        return convertToVO(community);
    }

    @Override
    public CommunityVO update(Long id, CommunityRequest request) {
        Community community = getCommunityOrThrow(id);
        String name = normalizeRequiredText(request.getName());
        validateDuplicateName(name, id);

        fillCommunity(community, request, name);
        communityMapper.updateById(community);
        return convertToVO(community);
    }

    @Override
    public Page<CommunityVO> getPage(Integer pageNum, Integer pageSize, String name) {
        Page<Community> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Community::getName, name.trim());
        }
        queryWrapper.orderByDesc(Community::getCreateTime);

        Page<Community> communityPage = communityMapper.selectPage(page, queryWrapper);
        Page<CommunityVO> voPage = new Page<>(communityPage.getCurrent(), communityPage.getSize(), communityPage.getTotal());
        voPage.setRecords(communityPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public CommunityVO getById(Long id) {
        return convertToVO(getCommunityOrThrow(id));
    }

    @Override
    public List<CommunityVO> getList() {
        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Community::getName);
        return communityMapper.selectList(queryWrapper).stream().map(this::convertToVO).toList();
    }

    @Override
    public void delete(Long id) {
        Community community = getCommunityOrThrow(id);

        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getCommunityId, id)
        );
        if (userCount > 0) {
            throw new BusinessException("该小区下仍有关联用户，无法删除");
        }

        Long buildingCount = buildingMapper.selectCount(
                new LambdaQueryWrapper<Building>().eq(Building::getCommunityId, id)
        );
        if (buildingCount > 0) {
            throw new BusinessException("该小区下仍有关联楼栋，无法删除");
        }

        communityMapper.deleteById(community.getId());
    }

    private Community getCommunityOrThrow(Long id) {
        Community community = communityMapper.selectById(id);
        if (community == null) {
            throw new BusinessException("小区不存在");
        }
        return community;
    }

    private void validateDuplicateName(String name, Long excludeId) {
        LambdaQueryWrapper<Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Community::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(Community::getId, excludeId);
        }
        if (communityMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("小区名称已存在");
        }
    }

    private void fillCommunity(Community community, CommunityRequest request, String name) {
        community.setName(name);
        community.setAddress(normalizeOptionalText(request.getAddress()));
        community.setArea(request.getArea());
        community.setBuildYear(request.getBuildYear());
        community.setTotalBuildings(request.getTotalBuildings());
        community.setTotalRooms(request.getTotalRooms());
        community.setDescription(normalizeOptionalText(request.getDescription()));
    }

    private CommunityVO convertToVO(Community community) {
        CommunityVO vo = new CommunityVO();
        BeanUtils.copyProperties(community, vo);
        if (community.getCreateTime() != null) {
            vo.setCreateTime(community.getCreateTime().format(FORMATTER));
        }
        if (community.getUpdateTime() != null) {
            vo.setUpdateTime(community.getUpdateTime().format(FORMATTER));
        }
        return vo;
    }

    private String normalizeRequiredText(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
