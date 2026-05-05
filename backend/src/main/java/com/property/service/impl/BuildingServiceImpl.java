package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.BuildingRequest;
import com.property.entity.Building;
import com.property.entity.Community;
import com.property.entity.Room;
import com.property.entity.User;
import com.property.mapper.BuildingMapper;
import com.property.mapper.CommunityMapper;
import com.property.mapper.RoomMapper;
import com.property.mapper.UserMapper;
import com.property.service.BuildingService;
import com.property.vo.BuildingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 楼栋服务实现
 */
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BuildingMapper buildingMapper;
    private final CommunityMapper communityMapper;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    @Override
    public BuildingVO create(BuildingRequest request) {
        Community community = getCommunityOrThrow(request.getCommunityId());
        String name = normalizeRequiredText(request.getName(), "楼栋名称不能为空");
        validateDuplicateName(request.getCommunityId(), name, null);

        Building building = new Building();
        fillBuilding(building, request, name);
        building.setDeleted(0);
        buildingMapper.insert(building);

        syncCommunityTotals(request.getCommunityId());
        return convertToVO(building, community);
    }

    @Override
    public BuildingVO update(Long id, BuildingRequest request) {
        Building building = getBuildingOrThrow(id);
        Long oldCommunityId = building.getCommunityId();

        Community community = getCommunityOrThrow(request.getCommunityId());
        String name = normalizeRequiredText(request.getName(), "楼栋名称不能为空");
        validateDuplicateName(request.getCommunityId(), name, id);

        fillBuilding(building, request, name);
        buildingMapper.updateById(building);

        syncCommunityTotals(oldCommunityId);
        if (!oldCommunityId.equals(request.getCommunityId())) {
            syncCommunityTotals(request.getCommunityId());
        }
        return convertToVO(building, community);
    }

    @Override
    public Page<BuildingVO> getPage(Integer pageNum, Integer pageSize, Long communityId, String name) {
        Page<Building> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        if (communityId != null) {
            queryWrapper.eq(Building::getCommunityId, communityId);
        }
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Building::getName, name.trim());
        }
        queryWrapper.orderByAsc(Building::getCommunityId)
                .orderByAsc(Building::getName)
                .orderByDesc(Building::getCreateTime);

        Page<Building> buildingPage = buildingMapper.selectPage(page, queryWrapper);
        Page<BuildingVO> voPage = new Page<>(buildingPage.getCurrent(), buildingPage.getSize(), buildingPage.getTotal());
        voPage.setRecords(buildingPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public BuildingVO getById(Long id) {
        return convertToVO(getBuildingOrThrow(id));
    }

    @Override
    public List<BuildingVO> getList(Long communityId) {
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        if (communityId != null) {
            queryWrapper.eq(Building::getCommunityId, communityId);
        }
        queryWrapper.orderByAsc(Building::getCommunityId).orderByAsc(Building::getName);
        return buildingMapper.selectList(queryWrapper).stream().map(this::convertToVO).toList();
    }

    @Override
    public void delete(Long id) {
        Building building = getBuildingOrThrow(id);

        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getBuildingId, id)
        );
        if (userCount > 0) {
            throw new BusinessException("该楼栋下仍有关联用户，无法删除");
        }

        Long roomCount = roomMapper.selectCount(
                new LambdaQueryWrapper<Room>().eq(Room::getBuildingId, id)
        );
        if (roomCount > 0) {
            throw new BusinessException("该楼栋下仍有关联房屋，无法删除");
        }

        buildingMapper.deleteById(id);
        syncCommunityTotals(building.getCommunityId());
    }

    private Building getBuildingOrThrow(Long id) {
        Building building = buildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        return building;
    }

    private Community getCommunityOrThrow(Long id) {
        Community community = communityMapper.selectById(id);
        if (community == null) {
            throw new BusinessException("小区不存在");
        }
        return community;
    }

    private void validateDuplicateName(Long communityId, String name, Long excludeId) {
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Building::getCommunityId, communityId)
                .eq(Building::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(Building::getId, excludeId);
        }
        if (buildingMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("同一小区下楼栋名称已存在");
        }
    }

    private void fillBuilding(Building building, BuildingRequest request, String name) {
        building.setCommunityId(request.getCommunityId());
        building.setName(name);
        building.setFloors(request.getFloors());
        building.setUnits(request.getUnits());
        building.setRoomsPerFloor(request.getRoomsPerFloor());
    }

    private void syncCommunityTotals(Long communityId) {
        if (communityId == null) {
            return;
        }

        Community community = communityMapper.selectById(communityId);
        if (community == null) {
            return;
        }

        long buildingCount = buildingMapper.selectCount(
                new LambdaQueryWrapper<Building>().eq(Building::getCommunityId, communityId)
        );

        List<Long> buildingIds = buildingMapper.selectList(
                new LambdaQueryWrapper<Building>()
                        .select(Building::getId)
                        .eq(Building::getCommunityId, communityId)
        ).stream().map(Building::getId).toList();

        long roomCount = buildingIds.isEmpty()
                ? 0L
                : roomMapper.selectCount(new LambdaQueryWrapper<Room>().in(Room::getBuildingId, buildingIds));

        community.setTotalBuildings(Math.toIntExact(buildingCount));
        community.setTotalRooms(Math.toIntExact(roomCount));
        communityMapper.updateById(community);
    }

    private BuildingVO convertToVO(Building building) {
        return convertToVO(building, communityMapper.selectById(building.getCommunityId()));
    }

    private BuildingVO convertToVO(Building building, Community community) {
        BuildingVO vo = new BuildingVO();
        BeanUtils.copyProperties(building, vo);
        if (community != null) {
            vo.setCommunityName(community.getName());
        }
        vo.setRoomCount(
                roomMapper.selectCount(
                        new LambdaQueryWrapper<Room>().eq(Room::getBuildingId, building.getId())
                )
        );
        if (building.getCreateTime() != null) {
            vo.setCreateTime(building.getCreateTime().format(FORMATTER));
        }
        if (building.getUpdateTime() != null) {
            vo.setUpdateTime(building.getUpdateTime().format(FORMATTER));
        }
        return vo;
    }

    private String normalizeRequiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
        return value.trim();
    }
}
