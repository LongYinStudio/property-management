package com.property.service.impl;

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
import com.property.vo.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private CommunityMapper communityMapper;

    @Mock
    private BuildingMapper buildingMapper;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldOccupyRoomWhenCreatingOwner() {
        UserRequest request = new UserRequest();
        request.setUsername("owner001");
        request.setPassword("123456");
        request.setRealName("业主一");
        request.setRole(User.ROLE_OWNER);
        request.setRoomId(301L);

        Room room = new Room();
        room.setId(301L);
        room.setBuildingId(201L);
        room.setRoomNumber("301");
        room.setStatus(Room.STATUS_VACANT);

        Building building = new Building();
        building.setId(201L);
        building.setName("1栋");
        building.setCommunityId(101L);

        Community community = new Community();
        community.setId(101L);
        community.setName("阳光花园");

        when(userMapper.selectCount(any())).thenReturn(0L, 0L);
        when(roomMapper.selectById(301L)).thenReturn(room);
        when(buildingMapper.selectById(201L)).thenReturn(building);
        when(communityMapper.selectById(101L)).thenReturn(community);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        UserVO result = userService.create(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User insertedUser = userCaptor.getValue();
        assertEquals("owner001", insertedUser.getUsername());
        assertEquals("encoded-password", insertedUser.getPassword());
        assertEquals(User.STATUS_ENABLE, insertedUser.getStatus());
        assertEquals(101L, insertedUser.getCommunityId());
        assertEquals(201L, insertedUser.getBuildingId());
        assertEquals(301L, insertedUser.getRoomId());

        assertEquals(1L, room.getOwnerId());
        assertEquals(Room.STATUS_OCCUPIED, room.getStatus());
        verify(roomMapper).updateById(room);

        assertNotNull(result);
        assertEquals("业主一", result.getRealName());
        assertEquals("阳光花园", result.getCommunityName());
        assertEquals("1栋 301", result.getRoomName());
    }

    @Test
    void createShouldRejectWhenUsernameAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setUsername("owner001");
        request.setPassword("123456");
        request.setRealName("业主一");
        request.setRole(User.ROLE_OWNER);

        when(userMapper.selectCount(any())).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(request));

        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void createShouldRejectWhenRoomAlreadyBoundToAnotherOwner() {
        UserRequest request = new UserRequest();
        request.setUsername("owner001");
        request.setPassword("123456");
        request.setRealName("业主一");
        request.setRole(User.ROLE_OWNER);
        request.setRoomId(301L);

        Room room = new Room();
        room.setId(301L);
        room.setBuildingId(201L);
        room.setOwnerId(99L);

        Building building = new Building();
        building.setId(201L);
        building.setCommunityId(101L);

        Community community = new Community();
        community.setId(101L);

        when(userMapper.selectCount(any())).thenReturn(0L);
        when(roomMapper.selectById(301L)).thenReturn(room);
        when(buildingMapper.selectById(201L)).thenReturn(building);
        when(communityMapper.selectById(101L)).thenReturn(community);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(request));

        assertEquals("该房屋已绑定其他业主", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
        verify(roomMapper, never()).updateById(any(Room.class));
    }

    @Test
    void updateShouldReleaseOldRoomAndOccupyNewRoomWhenOwnerMoves() {
        UserRequest request = new UserRequest();
        request.setUsername("owner001");
        request.setPassword("654321");
        request.setRealName("业主一");
        request.setRole(User.ROLE_OWNER);
        request.setRoomId(302L);
        request.setStatus(User.STATUS_ENABLE);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("owner001");
        existingUser.setPassword("old-password");
        existingUser.setRole(User.ROLE_OWNER);
        existingUser.setRoomId(301L);
        existingUser.setBuildingId(201L);
        existingUser.setCommunityId(101L);

        Room oldRoom = new Room();
        oldRoom.setId(301L);
        oldRoom.setOwnerId(1L);
        oldRoom.setStatus(Room.STATUS_OCCUPIED);

        Room newRoom = new Room();
        newRoom.setId(302L);
        newRoom.setBuildingId(202L);
        newRoom.setRoomNumber("302");
        newRoom.setStatus(Room.STATUS_VACANT);

        Building building = new Building();
        building.setId(202L);
        building.setCommunityId(101L);

        Community community = new Community();
        community.setId(101L);

        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(roomMapper.selectById(302L)).thenReturn(newRoom);
        when(roomMapper.selectById(301L)).thenReturn(oldRoom);
        when(buildingMapper.selectById(202L)).thenReturn(building);
        when(communityMapper.selectById(101L)).thenReturn(community);
        when(passwordEncoder.encode("654321")).thenReturn("encoded-new-password");

        UserVO result = userService.update(1L, request);

        assertEquals("encoded-new-password", existingUser.getPassword());
        assertEquals(302L, existingUser.getRoomId());

        assertNull(oldRoom.getOwnerId());
        assertEquals(Room.STATUS_VACANT, oldRoom.getStatus());
        assertEquals(1L, newRoom.getOwnerId());
        assertEquals(Room.STATUS_OCCUPIED, newRoom.getStatus());
        verify(roomMapper).updateById(oldRoom);
        verify(roomMapper).updateById(newRoom);

        assertNotNull(result);
        assertEquals(302L, result.getRoomId());
    }

    @Test
    void deleteShouldRejectDeletingCurrentUser() {
        setCurrentUser(1L, User.ROLE_ADMIN);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.delete(1L));

        assertEquals("不能删除自己的账号", exception.getMessage());
        verify(userMapper, never()).deleteById(any());
    }

    @Test
    void deleteShouldRejectDeletingAdminAccount() {
        setCurrentUser(2L, User.ROLE_ADMIN);

        User admin = new User();
        admin.setId(1L);
        admin.setRole(User.ROLE_ADMIN);

        when(userMapper.selectById(1L)).thenReturn(admin);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.delete(1L));

        assertEquals("不能删除管理员账号", exception.getMessage());
        verify(userMapper, never()).deleteById(1L);
    }

    @Test
    void updateStatusShouldPersistForNonAdminUser() {
        setCurrentUser(2L, User.ROLE_ADMIN);

        User staff = new User();
        staff.setId(3L);
        staff.setRole(User.ROLE_STAFF);
        staff.setStatus(User.STATUS_ENABLE);

        when(userMapper.selectById(3L)).thenReturn(staff);

        userService.updateStatus(3L, User.STATUS_DISABLE);

        assertEquals(User.STATUS_DISABLE, staff.getStatus());
        verify(userMapper).updateById(staff);
    }

    private void setCurrentUser(Long userId, Integer role) {
        User user = new User();
        user.setId(userId);
        user.setUsername("tester");
        user.setRole(role);
        user.setStatus(User.STATUS_ENABLE);

        LoginUser loginUser = new LoginUser(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
