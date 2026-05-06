package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.UserRequest;
import com.property.entity.Building;
import com.property.entity.Community;
import com.property.entity.Room;
import com.property.entity.User;
import com.property.mapper.BuildingMapper;
import com.property.mapper.CommunityMapper;
import com.property.mapper.RoomMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.UserService;
import com.property.util.FileUrlUtils;
import com.property.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final CommunityMapper communityMapper;
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final PasswordEncoder passwordEncoder;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public UserVO create(UserRequest request) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        ResidenceBinding binding = resolveResidenceBinding(
                request.getCommunityId(),
                request.getBuildingId(),
                request.getRoomId()
        );
        validateRoomAssignment(binding.room(), request.getRole(), null);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(StringUtils.hasText(request.getPassword()) ? request.getPassword() : "123456"));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(FileUrlUtils.normalizeUploadUrl(request.getAvatar()));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus() != null ? request.getStatus() : User.STATUS_ENABLE);
        user.setCommunityId(binding.communityId());
        user.setBuildingId(binding.buildingId());
        user.setRoomId(binding.roomId());
        user.setDeleted(0);
        
        userMapper.insert(user);
        syncRoomOwnership(null, user);
        
        return convertToVO(user);
    }
    
    @Override
    public UserVO update(Long id, UserRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        ResidenceBinding binding = resolveResidenceBinding(
                request.getCommunityId(),
                request.getBuildingId(),
                request.getRoomId()
        );
        validateRoomAssignment(binding.room(), request.getRole(), id);

        User oldSnapshot = copyUser(user);

        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(request.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, request.getUsername());
            if (userMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(request.getUsername());
        }
        
        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(FileUrlUtils.normalizeUploadUrl(request.getAvatar()));
        user.setRole(request.getRole());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setCommunityId(binding.communityId());
        user.setBuildingId(binding.buildingId());
        user.setRoomId(binding.roomId());
        
        userMapper.updateById(user);
        syncRoomOwnership(oldSnapshot, user);
        
        return convertToVO(user);
    }
    
    @Override
    public Page<UserVO> getPage(Integer pageNum, Integer pageSize, String username, Integer role) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (role != null) {
            queryWrapper.eq(User::getRole, role);
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }
    
    @Override
    public void delete(Long id) {
        // 不能删除自己
        Long currentUserId = getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new BusinessException("不能删除自己的账号");
        }
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不能删除管理员账号
        if (user.getRole().equals(User.ROLE_ADMIN)) {
            throw new BusinessException("不能删除管理员账号");
        }

        clearRoomOwnershipIfNeeded(user);
        
        userMapper.deleteById(id);
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        // 不能修改自己的状态
        Long currentUserId = getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new BusinessException("不能修改自己的状态");
        }
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不能修改管理员状态
        if (user.getRole().equals(User.ROLE_ADMIN)) {
            throw new BusinessException("不能修改管理员状态");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
    }
    
    @Override
    public List<UserVO> getOwnerList() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, User.ROLE_OWNER);
        queryWrapper.eq(User::getStatus, User.STATUS_ENABLE);
        queryWrapper.orderByAsc(User::getRealName);
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<UserVO> getStaffList() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, User.ROLE_STAFF);
        queryWrapper.eq(User::getStatus, User.STATUS_ENABLE);
        queryWrapper.orderByAsc(User::getRealName);

        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::convertToVO).toList();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
    
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setAvatar(FileUrlUtils.normalizeUploadUrl(vo.getAvatar()));
        if (user.getCommunityId() != null) {
            Community community = communityMapper.selectById(user.getCommunityId());
            if (community != null) {
                vo.setCommunityName(community.getName());
            }
        }
        if (user.getBuildingId() != null) {
            Building building = buildingMapper.selectById(user.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }
        if (user.getRoomId() != null) {
            Room room = roomMapper.selectById(user.getRoomId());
            if (room != null) {
                vo.setRoomName(buildRoomName(user.getBuildingId(), room));
            }
        }
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(FORMATTER));
        }
        return vo;
    }

    private ResidenceBinding resolveResidenceBinding(Long communityId, Long buildingId, Long roomId) {
        Community community = null;
        Building building = null;
        Room room = null;

        if (roomId != null) {
            room = roomMapper.selectById(roomId);
            if (room == null) {
                throw new BusinessException("房屋不存在");
            }
            building = buildingMapper.selectById(room.getBuildingId());
            if (building == null) {
                throw new BusinessException("房屋所属楼栋不存在");
            }
            if (buildingId != null && !Objects.equals(buildingId, building.getId())) {
                throw new BusinessException("房屋与楼栋不匹配");
            }
            if (communityId != null && !Objects.equals(communityId, building.getCommunityId())) {
                throw new BusinessException("房屋与小区不匹配");
            }
            buildingId = building.getId();
            communityId = building.getCommunityId();
        }

        if (buildingId != null) {
            building = building != null ? building : buildingMapper.selectById(buildingId);
            if (building == null) {
                throw new BusinessException("楼栋不存在");
            }
            if (communityId != null && !Objects.equals(communityId, building.getCommunityId())) {
                throw new BusinessException("楼栋与小区不匹配");
            }
            communityId = building.getCommunityId();
        }

        if (communityId != null) {
            community = communityMapper.selectById(communityId);
            if (community == null) {
                throw new BusinessException("小区不存在");
            }
        }

        return new ResidenceBinding(
                community == null ? communityId : community.getId(),
                building == null ? buildingId : building.getId(),
                room == null ? roomId : room.getId(),
                room
        );
    }

    private void validateRoomAssignment(Room room, Integer role, Long currentUserId) {
        if (role == null || !Objects.equals(role, User.ROLE_OWNER) || room == null) {
            return;
        }

        if (room.getOwnerId() != null && !Objects.equals(room.getOwnerId(), currentUserId)) {
            throw new BusinessException("该房屋已绑定其他业主");
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRoomId, room.getId()).eq(User::getRole, User.ROLE_OWNER);
        if (currentUserId != null) {
            queryWrapper.ne(User::getId, currentUserId);
        }
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("该房屋已绑定其他业主");
        }
    }

    private void syncRoomOwnership(User oldUser, User newUser) {
        if (oldUser != null) {
            boolean needRelease = Objects.equals(oldUser.getRole(), User.ROLE_OWNER)
                    && (!Objects.equals(oldUser.getRoomId(), newUser.getRoomId())
                    || !Objects.equals(newUser.getRole(), User.ROLE_OWNER));
            if (needRelease) {
                releaseRoomOwnership(oldUser.getRoomId(), oldUser.getId());
            }
        }

        if (Objects.equals(newUser.getRole(), User.ROLE_OWNER) && newUser.getRoomId() != null) {
            occupyRoom(newUser.getRoomId(), newUser.getId());
        }
    }

    private void clearRoomOwnershipIfNeeded(User user) {
        if (Objects.equals(user.getRole(), User.ROLE_OWNER) && user.getRoomId() != null) {
            releaseRoomOwnership(user.getRoomId(), user.getId());
        }
    }

    private void occupyRoom(Long roomId, Long userId) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException("房屋不存在");
        }
        room.setOwnerId(userId);
        room.setStatus(Room.STATUS_OCCUPIED);
        roomMapper.updateById(room);
    }

    private void releaseRoomOwnership(Long roomId, Long userId) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            return;
        }
        if (!Objects.equals(room.getOwnerId(), userId)) {
            return;
        }
        room.setOwnerId(null);
        room.setStatus(Room.STATUS_VACANT);
        roomMapper.updateById(room);
    }

    private String buildRoomName(Long buildingId, Room room) {
        String buildingName = null;
        if (buildingId != null) {
            Building building = buildingMapper.selectById(buildingId);
            if (building != null) {
                buildingName = building.getName();
            }
        }

        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(buildingName)) {
            builder.append(buildingName);
        }
        if (StringUtils.hasText(room.getUnit())) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(room.getUnit()).append("单元");
        }
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(room.getRoomNumber());
        return builder.toString();
    }

    private User copyUser(User user) {
        User copy = new User();
        BeanUtils.copyProperties(user, copy);
        return copy;
    }

    private record ResidenceBinding(Long communityId, Long buildingId, Long roomId, Room room) {
    }
}
