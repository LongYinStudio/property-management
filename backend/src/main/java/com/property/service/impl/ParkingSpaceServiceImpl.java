package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.ParkingRentalRequest;
import com.property.dto.ParkingSpaceRequest;
import com.property.entity.ParkingRental;
import com.property.entity.ParkingSpace;
import com.property.entity.User;
import com.property.mapper.ParkingRentalMapper;
import com.property.mapper.ParkingSpaceMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.ParkingSpaceService;
import com.property.vo.ParkingRentalVO;
import com.property.vo.ParkingSpaceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 车位服务实现
 */
@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImpl implements ParkingSpaceService {
    
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final ParkingRentalMapper parkingRentalMapper;
    private final UserMapper userMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public ParkingSpaceVO createSpace(ParkingSpaceRequest request) {
        // 检查车位编号是否已存在
        LambdaQueryWrapper<ParkingSpace> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkingSpace::getSpaceNumber, request.getSpaceNumber());
        if (parkingSpaceMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("车位编号已存在");
        }
        
        ParkingSpace space = new ParkingSpace();
        space.setSpaceNumber(request.getSpaceNumber());
        space.setLocation(request.getLocation());
        space.setType(request.getType());
        space.setRentPrice(request.getRentPrice());
        space.setPrice(request.getPrice());
        space.setRemark(request.getRemark());
        space.setStatus(ParkingSpace.STATUS_AVAILABLE);
        space.setDeleted(0);
        
        parkingSpaceMapper.insert(space);
        
        return convertSpaceToVO(space);
    }
    
    @Override
    public ParkingSpaceVO updateSpace(Long id, ParkingSpaceRequest request) {
        ParkingSpace space = parkingSpaceMapper.selectById(id);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }
        
        // 检查车位编号是否重复
        if (!space.getSpaceNumber().equals(request.getSpaceNumber())) {
            LambdaQueryWrapper<ParkingSpace> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ParkingSpace::getSpaceNumber, request.getSpaceNumber());
            if (parkingSpaceMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("车位编号已存在");
            }
        }
        
        space.setSpaceNumber(request.getSpaceNumber());
        space.setLocation(request.getLocation());
        space.setType(request.getType());
        space.setRentPrice(request.getRentPrice());
        space.setPrice(request.getPrice());
        space.setRemark(request.getRemark());
        
        parkingSpaceMapper.updateById(space);
        
        return convertSpaceToVO(space);
    }
    
    @Override
    public Page<ParkingSpaceVO> getSpacePage(Integer pageNum, Integer pageSize, Integer status, Integer type) {
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ParkingSpace> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(ParkingSpace::getStatus, status);
        }
        if (type != null) {
            queryWrapper.eq(ParkingSpace::getType, type);
        }
        queryWrapper.orderByDesc(ParkingSpace::getCreateTime);
        
        Page<ParkingSpace> spacePage = parkingSpaceMapper.selectPage(page, queryWrapper);
        
        Page<ParkingSpaceVO> voPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        voPage.setRecords(spacePage.getRecords().stream().map(this::convertSpaceToVO).toList());
        
        return voPage;
    }
    
    @Override
    public ParkingSpaceVO getSpaceById(Long id) {
        ParkingSpace space = parkingSpaceMapper.selectById(id);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }
        
        return convertSpaceToVO(space);
    }
    
    @Override
    public void deleteSpace(Long id) {
        ParkingSpace space = parkingSpaceMapper.selectById(id);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }
        
        if (space.getStatus() != ParkingSpace.STATUS_AVAILABLE) {
            throw new BusinessException("车位已被占用，无法删除");
        }
        
        parkingSpaceMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public ParkingSpaceVO assignSpace(Long id, Long userId) {
        ParkingSpace space = parkingSpaceMapper.selectById(id);
        if (space == null) {
            throw new BusinessException("车位不存在");
        }
        
        if (space.getStatus() == ParkingSpace.STATUS_SOLD) {
            throw new BusinessException("车位已出售");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        space.setOwnerId(userId);
        space.setStatus(ParkingSpace.STATUS_SOLD);
        parkingSpaceMapper.updateById(space);
        
        return convertSpaceToVO(space);
    }
    
    @Override
    @Transactional
    public ParkingRentalVO rentSpace(ParkingRentalRequest request) {
        ParkingSpace space = parkingSpaceMapper.selectById(request.getSpaceId());
        if (space == null) {
            throw new BusinessException("车位不存在");
        }
        
        if (space.getStatus() == ParkingSpace.STATUS_SOLD) {
            throw new BusinessException("车位已出售，无法租用");
        }
        
        if (space.getStatus() == ParkingSpace.STATUS_RENTED) {
            throw new BusinessException("车位已出租");
        }
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        
        ParkingRental rental = new ParkingRental();
        rental.setSpaceId(request.getSpaceId());
        rental.setUserId(currentUser.getId());
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setAmount(request.getAmount());
        rental.setStatus(ParkingRental.STATUS_UNPAID);
        rental.setDeleted(0);
        
        parkingRentalMapper.insert(rental);
        
        // 更新车位状态
        space.setStatus(ParkingSpace.STATUS_RENTED);
        parkingSpaceMapper.updateById(space);
        
        return convertRentalToVO(rental);
    }
    
    @Override
    @Transactional
    public ParkingRentalVO renewSpace(Long rentalId, ParkingRentalRequest request) {
        ParkingRental oldRental = parkingRentalMapper.selectById(rentalId);
        if (oldRental == null) {
            throw new BusinessException("租用记录不存在");
        }
        
        if (oldRental.getStatus() != ParkingRental.STATUS_PAID) {
            throw new BusinessException("原租用记录未支付，无法续费");
        }
        
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        
        // 验证权限
        if (currentUser.getRole() == User.ROLE_OWNER && !oldRental.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("无权续费该车位");
        }
        
        ParkingRental rental = new ParkingRental();
        rental.setSpaceId(oldRental.getSpaceId());
        rental.setUserId(currentUser.getId());
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setAmount(request.getAmount());
        rental.setStatus(ParkingRental.STATUS_UNPAID);
        rental.setDeleted(0);
        
        parkingRentalMapper.insert(rental);
        
        return convertRentalToVO(rental);
    }
    
    @Override
    public Page<ParkingRentalVO> getRentalPage(Integer pageNum, Integer pageSize, Long spaceId, Integer status) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        
        Page<ParkingRental> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ParkingRental> queryWrapper = new LambdaQueryWrapper<>();
        
        // 业主只能查看自己的租用记录
        if (currentUser.getRole() == User.ROLE_OWNER) {
            queryWrapper.eq(ParkingRental::getUserId, currentUser.getId());
        }
        
        if (spaceId != null) {
            queryWrapper.eq(ParkingRental::getSpaceId, spaceId);
        }
        if (status != null) {
            queryWrapper.eq(ParkingRental::getStatus, status);
        }
        queryWrapper.orderByDesc(ParkingRental::getCreateTime);
        
        Page<ParkingRental> rentalPage = parkingRentalMapper.selectPage(page, queryWrapper);
        
        Page<ParkingRentalVO> voPage = new Page<>(rentalPage.getCurrent(), rentalPage.getSize(), rentalPage.getTotal());
        voPage.setRecords(rentalPage.getRecords().stream().map(this::convertRentalToVO).toList());
        
        return voPage;
    }
    
    @Override
    @Transactional
    public void payRental(Long id) {
        ParkingRental rental = parkingRentalMapper.selectById(id);
        if (rental == null) {
            throw new BusinessException("租用记录不存在");
        }
        
        if (rental.getStatus() == ParkingRental.STATUS_PAID) {
            throw new BusinessException("该租用已支付");
        }
        
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        
        // 验证权限
        if (currentUser.getRole() == User.ROLE_OWNER && !rental.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("无权支付该租用");
        }
        
        rental.setStatus(ParkingRental.STATUS_PAID);
        rental.setPayTime(LocalDateTime.now());
        parkingRentalMapper.updateById(rental);
    }
    
    @Override
    @Transactional
    public void cancelRental(Long id) {
        ParkingRental rental = parkingRentalMapper.selectById(id);
        if (rental == null) {
            throw new BusinessException("租用记录不存在");
        }
        
        if (rental.getStatus() == ParkingRental.STATUS_PAID) {
            throw new BusinessException("已支付的租用无法取消");
        }
        
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        
        // 验证权限
        if (currentUser.getRole() == User.ROLE_OWNER && !rental.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("无权取消该租用");
        }
        
        // 删除租用记录
        parkingRentalMapper.deleteById(id);
        
        // 更新车位状态
        ParkingSpace space = parkingSpaceMapper.selectById(rental.getSpaceId());
        if (space != null && space.getStatus() == ParkingSpace.STATUS_RENTED) {
            space.setStatus(ParkingSpace.STATUS_AVAILABLE);
            parkingSpaceMapper.updateById(space);
        }
    }
    
    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return (LoginUser) authentication.getPrincipal();
    }
    
    private ParkingSpaceVO convertSpaceToVO(ParkingSpace space) {
        ParkingSpaceVO vo = new ParkingSpaceVO();
        BeanUtils.copyProperties(space, vo);
        
        // 设置业主名称
        if (space.getOwnerId() != null) {
            User owner = userMapper.selectById(space.getOwnerId());
            if (owner != null) {
                vo.setOwnerName(owner.getRealName());
            }
        }
        
        // 格式化时间
        if (space.getCreateTime() != null) {
            vo.setCreateTime(space.getCreateTime().format(FORMATTER));
        }
        
        return vo;
    }
    
    private ParkingRentalVO convertRentalToVO(ParkingRental rental) {
        ParkingRentalVO vo = new ParkingRentalVO();
        BeanUtils.copyProperties(rental, vo);
        
        // 设置车位编号
        ParkingSpace space = parkingSpaceMapper.selectById(rental.getSpaceId());
        if (space != null) {
            vo.setSpaceNumber(space.getSpaceNumber());
        }
        
        // 设置用户名
        User user = userMapper.selectById(rental.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }
        
        // 格式化时间
        if (rental.getStartDate() != null) {
            vo.setStartDate(rental.getStartDate().format(DATE_FORMATTER));
        }
        if (rental.getEndDate() != null) {
            vo.setEndDate(rental.getEndDate().format(DATE_FORMATTER));
        }
        if (rental.getCreateTime() != null) {
            vo.setCreateTime(rental.getCreateTime().format(FORMATTER));
        }
        if (rental.getPayTime() != null) {
            vo.setPayTime(rental.getPayTime().format(FORMATTER));
        }
        
        return vo;
    }
}
