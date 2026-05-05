package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.RoomRequest;
import com.property.entity.Building;
import com.property.entity.Community;
import com.property.entity.PropertyFee;
import com.property.entity.Room;
import com.property.entity.User;
import com.property.mapper.BuildingMapper;
import com.property.mapper.CommunityMapper;
import com.property.mapper.PropertyFeeMapper;
import com.property.mapper.RoomMapper;
import com.property.mapper.UserMapper;
import com.property.service.RoomService;
import com.property.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 房屋服务实现
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    private final CommunityMapper communityMapper;
    private final UserMapper userMapper;
    private final PropertyFeeMapper propertyFeeMapper;

    @Override
    public RoomVO create(RoomRequest request) {
        Building building = getBuildingOrThrow(request.getBuildingId());
        String unit = normalizeOptionalText(request.getUnit());
        String roomNumber = normalizeRequiredText(request.getRoomNumber(), "房号不能为空");
        validateDuplicateRoom(request.getBuildingId(), unit, roomNumber, null);

        Room room = new Room();
        fillRoom(room, request, unit, roomNumber);
        room.setDeleted(0);
        roomMapper.insert(room);

        syncCommunityTotals(building.getCommunityId());
        return convertToVO(room, building, communityMapper.selectById(building.getCommunityId()), null);
    }

    @Override
    public RoomVO update(Long id, RoomRequest request) {
        Room room = getRoomOrThrow(id);
        Building oldBuilding = getBuildingOrThrow(room.getBuildingId());

        Building building = getBuildingOrThrow(request.getBuildingId());
        String unit = normalizeOptionalText(request.getUnit());
        String roomNumber = normalizeRequiredText(request.getRoomNumber(), "房号不能为空");
        validateDuplicateRoom(request.getBuildingId(), unit, roomNumber, id);

        fillRoom(room, request, unit, roomNumber);
        if (room.getOwnerId() != null) {
            room.setStatus(Room.STATUS_OCCUPIED);
        }
        roomMapper.updateById(room);

        syncCommunityTotals(oldBuilding.getCommunityId());
        if (!oldBuilding.getCommunityId().equals(building.getCommunityId())) {
            syncCommunityTotals(building.getCommunityId());
        }
        return convertToVO(room, building, communityMapper.selectById(building.getCommunityId()), null);
    }

    @Override
    public Page<RoomVO> getPage(Integer pageNum, Integer pageSize, Long communityId, Long buildingId, String roomNumber, Integer status) {
        Page<Room> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();

        List<Long> scopedBuildingIds = resolveBuildingIds(communityId, buildingId);
        if (scopedBuildingIds != null) {
            if (scopedBuildingIds.isEmpty()) {
                return new Page<>(pageNum, pageSize, 0);
            }
            queryWrapper.in(Room::getBuildingId, scopedBuildingIds);
        }

        if (StringUtils.hasText(roomNumber)) {
            queryWrapper.like(Room::getRoomNumber, roomNumber.trim());
        }
        if (status != null) {
            queryWrapper.eq(Room::getStatus, status);
        }
        queryWrapper.orderByAsc(Room::getBuildingId)
                .orderByAsc(Room::getFloor)
                .orderByAsc(Room::getRoomNumber)
                .orderByDesc(Room::getCreateTime);

        Page<Room> roomPage = roomMapper.selectPage(page, queryWrapper);
        Page<RoomVO> voPage = new Page<>(roomPage.getCurrent(), roomPage.getSize(), roomPage.getTotal());
        voPage.setRecords(roomPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public RoomVO getById(Long id) {
        return convertToVO(getRoomOrThrow(id));
    }

    @Override
    public List<RoomVO> getList(Long communityId, Long buildingId) {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        List<Long> scopedBuildingIds = resolveBuildingIds(communityId, buildingId);
        if (scopedBuildingIds != null) {
            if (scopedBuildingIds.isEmpty()) {
                return List.of();
            }
            queryWrapper.in(Room::getBuildingId, scopedBuildingIds);
        }
        queryWrapper.orderByAsc(Room::getBuildingId).orderByAsc(Room::getFloor).orderByAsc(Room::getRoomNumber);
        return roomMapper.selectList(queryWrapper).stream().map(this::convertToVO).toList();
    }

    @Override
    public void delete(Long id) {
        Room room = getRoomOrThrow(id);
        Building building = getBuildingOrThrow(room.getBuildingId());

        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRoomId, id)
        );
        if (userCount > 0) {
            throw new BusinessException("该房屋下仍有关联用户，无法删除");
        }

        Long feeCount = propertyFeeMapper.selectCount(
                new LambdaQueryWrapper<PropertyFee>().eq(PropertyFee::getRoomId, id)
        );
        if (feeCount > 0) {
            throw new BusinessException("该房屋已有关联物业费记录，无法删除");
        }

        roomMapper.deleteById(id);
        syncCommunityTotals(building.getCommunityId());
    }

    private Room getRoomOrThrow(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException("房屋不存在");
        }
        return room;
    }

    private Building getBuildingOrThrow(Long id) {
        Building building = buildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        return building;
    }

    private void validateDuplicateRoom(Long buildingId, String unit, String roomNumber, Long excludeId) {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getBuildingId, buildingId)
                .eq(Room::getRoomNumber, roomNumber);
        if (StringUtils.hasText(unit)) {
            queryWrapper.eq(Room::getUnit, unit);
        } else {
            queryWrapper.isNull(Room::getUnit);
        }
        if (excludeId != null) {
            queryWrapper.ne(Room::getId, excludeId);
        }
        if (roomMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("同一楼栋下房号已存在");
        }
    }

    private void fillRoom(Room room, RoomRequest request, String unit, String roomNumber) {
        room.setBuildingId(request.getBuildingId());
        room.setUnit(unit);
        room.setFloor(request.getFloor());
        room.setRoomNumber(roomNumber);
        room.setArea(request.getArea());
        room.setStatus(request.getStatus() != null ? request.getStatus() : Room.STATUS_VACANT);
        if (room.getOwnerId() == null && room.getStatus() == null) {
            room.setStatus(Room.STATUS_VACANT);
        }
    }

    private void syncCommunityTotals(Long communityId) {
        if (communityId == null) {
            return;
        }

        Community community = communityMapper.selectById(communityId);
        if (community == null) {
            return;
        }

        List<Long> buildingIds = buildingMapper.selectList(
                new LambdaQueryWrapper<Building>()
                        .select(Building::getId)
                        .eq(Building::getCommunityId, communityId)
        ).stream().map(Building::getId).toList();

        long roomCount = buildingIds.isEmpty()
                ? 0L
                : roomMapper.selectCount(new LambdaQueryWrapper<Room>().in(Room::getBuildingId, buildingIds));

        long buildingCount = buildingIds.size();
        community.setTotalBuildings(Math.toIntExact(buildingCount));
        community.setTotalRooms(Math.toIntExact(roomCount));
        communityMapper.updateById(community);
    }

    private List<Long> resolveBuildingIds(Long communityId, Long buildingId) {
        if (buildingId != null) {
            return List.of(buildingId);
        }
        if (communityId == null) {
            return null;
        }
        return buildingMapper.selectList(
                new LambdaQueryWrapper<Building>()
                        .select(Building::getId)
                        .eq(Building::getCommunityId, communityId)
        ).stream().map(Building::getId).toList();
    }

    private RoomVO convertToVO(Room room) {
        Building building = buildingMapper.selectById(room.getBuildingId());
        Community community = building == null ? null : communityMapper.selectById(building.getCommunityId());
        User owner = resolveOwner(room);
        return convertToVO(room, building, community, owner);
    }

    private RoomVO convertToVO(Room room, Building building, Community community, User owner) {
        RoomVO vo = new RoomVO();
        BeanUtils.copyProperties(room, vo);
        if (building != null) {
            vo.setBuildingName(building.getName());
        }
        if (community != null) {
            vo.setCommunityId(community.getId());
            vo.setCommunityName(community.getName());
        }
        if (owner != null) {
            vo.setOwnerId(owner.getId());
            vo.setOwnerName(owner.getRealName());
            vo.setStatus(Room.STATUS_OCCUPIED);
        }
        vo.setDisplayName(buildDisplayName(building == null ? null : building.getName(), room.getUnit(), room.getRoomNumber()));
        if (room.getCreateTime() != null) {
            vo.setCreateTime(room.getCreateTime().format(FORMATTER));
        }
        if (room.getUpdateTime() != null) {
            vo.setUpdateTime(room.getUpdateTime().format(FORMATTER));
        }
        return vo;
    }

    private User resolveOwner(Room room) {
        if (room.getOwnerId() != null) {
            User owner = userMapper.selectById(room.getOwnerId());
            if (owner != null) {
                return owner;
            }
        }
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRoomId, room.getId())
                        .eq(User::getRole, User.ROLE_OWNER)
                        .last("limit 1")
        );
    }

    private String buildDisplayName(String buildingName, String unit, String roomNumber) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.hasText(buildingName)) {
            parts.add(buildingName);
        }
        if (StringUtils.hasText(unit)) {
            parts.add(unit + "单元");
        }
        parts.add(roomNumber);
        return String.join(" ", parts);
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
