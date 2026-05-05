package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.dto.PropertyFeeRequest;
import com.property.entity.PropertyFee;
import com.property.entity.User;
import com.property.mapper.PropertyFeeMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.vo.PropertyFeeVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyFeeServiceImplTest {

    @Mock
    private PropertyFeeMapper propertyFeeMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private PropertyFeeServiceImpl propertyFeeService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldInitializeUnpaidFee() {
        PropertyFeeRequest request = new PropertyFeeRequest();
        request.setUserId(1L);
        request.setRoomId(301L);
        request.setYear(2026);
        request.setMonth(5);
        request.setAmount(new BigDecimal("288.50"));
        request.setType(PropertyFee.TYPE_PROPERTY);
        request.setDescription("5月物业费");

        User user = new User();
        user.setId(1L);
        user.setRealName("张三");

        when(userMapper.selectById(1L)).thenReturn(user);
        doAnswer(invocation -> {
            PropertyFee fee = invocation.getArgument(0);
            fee.setId(10L);
            return 1;
        }).when(propertyFeeMapper).insert(any(PropertyFee.class));

        PropertyFeeVO result = propertyFeeService.create(request);

        verify(propertyFeeMapper).insert(any(PropertyFee.class));
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(PropertyFee.STATUS_UNPAID, result.getStatus());
        assertEquals("张三", result.getUserName());
    }

    @Test
    void payShouldRejectWhenOwnerPaysAnotherUsersFee() {
        setCurrentUser(2L, User.ROLE_OWNER);

        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setId(1L);
        propertyFee.setUserId(1L);
        propertyFee.setStatus(PropertyFee.STATUS_UNPAID);

        when(propertyFeeMapper.selectById(1L)).thenReturn(propertyFee);

        BusinessException exception = assertThrows(BusinessException.class, () -> propertyFeeService.pay(1L));

        assertEquals("无权支付该物业费", exception.getMessage());
        verify(propertyFeeMapper, never()).updateById(any(PropertyFee.class));
    }

    @Test
    void payShouldMarkFeeAsPaidForOwnerSelf() {
        setCurrentUser(1L, User.ROLE_OWNER);

        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setId(1L);
        propertyFee.setUserId(1L);
        propertyFee.setStatus(PropertyFee.STATUS_UNPAID);

        when(propertyFeeMapper.selectById(1L)).thenReturn(propertyFee);

        propertyFeeService.pay(1L);

        assertEquals(PropertyFee.STATUS_PAID, propertyFee.getStatus());
        assertNotNull(propertyFee.getPayTime());
        verify(propertyFeeMapper).updateById(propertyFee);
    }

    @Test
    void payShouldRejectWhenFeeAlreadyPaid() {
        setCurrentUser(1L, User.ROLE_OWNER);

        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setId(1L);
        propertyFee.setUserId(1L);
        propertyFee.setStatus(PropertyFee.STATUS_PAID);

        when(propertyFeeMapper.selectById(1L)).thenReturn(propertyFee);

        BusinessException exception = assertThrows(BusinessException.class, () -> propertyFeeService.pay(1L));

        assertEquals("该物业费已支付", exception.getMessage());
        verify(propertyFeeMapper, never()).updateById(any(PropertyFee.class));
    }

    @Test
    void getByIdShouldRejectWhenFeeDoesNotExist() {
        setCurrentUser(1L, User.ROLE_ADMIN);
        when(propertyFeeMapper.selectById(99L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> propertyFeeService.getById(99L));

        assertEquals("物业费记录不存在", exception.getMessage());
    }

    @Test
    void getByIdShouldReturnFeeForAdmin() {
        setCurrentUser(9L, User.ROLE_ADMIN);

        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setId(1L);
        propertyFee.setUserId(1L);
        propertyFee.setStatus(PropertyFee.STATUS_UNPAID);
        propertyFee.setAmount(new BigDecimal("300.00"));

        User owner = new User();
        owner.setId(1L);
        owner.setRealName("张三");

        when(propertyFeeMapper.selectById(1L)).thenReturn(propertyFee);
        when(userMapper.selectById(1L)).thenReturn(owner);

        PropertyFeeVO result = propertyFeeService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("张三", result.getUserName());
        assertTrue(result.getAmount().compareTo(new BigDecimal("300.00")) == 0);
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
