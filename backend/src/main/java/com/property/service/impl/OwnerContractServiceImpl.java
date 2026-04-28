package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.OwnerContractRequest;
import com.property.entity.OwnerContract;
import com.property.entity.User;
import com.property.mapper.OwnerContractMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.OwnerContractService;
import com.property.vo.OwnerContractVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;

/**
 * 业主合同服务实现
 */
@Service
@RequiredArgsConstructor
public class OwnerContractServiceImpl implements OwnerContractService {

    private final OwnerContractMapper ownerContractMapper;
    private final UserMapper userMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public OwnerContractVO create(OwnerContractRequest request) {
        validateRequest(request, null);

        LoginUser loginUser = getCurrentLoginUser();
        OwnerContract ownerContract = new OwnerContract();
        BeanUtils.copyProperties(request, ownerContract);
        ownerContract.setCreatorId(loginUser.getUser().getId());
        ownerContract.setDeleted(0);

        ownerContractMapper.insert(ownerContract);
        return convertToVO(ownerContract);
    }

    @Override
    public OwnerContractVO update(Long id, OwnerContractRequest request) {
        OwnerContract ownerContract = ownerContractMapper.selectById(id);
        if (ownerContract == null) {
            throw new BusinessException("合同记录不存在");
        }

        validateRequest(request, id);

        BeanUtils.copyProperties(request, ownerContract);
        ownerContractMapper.updateById(ownerContract);
        return convertToVO(ownerContract);
    }

    @Override
    public Page<OwnerContractVO> getPage(
            Integer pageNum,
            Integer pageSize,
            String keyword,
            Integer status,
            Integer contractType,
            Long userId
    ) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();

        Page<OwnerContract> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OwnerContract> queryWrapper = new LambdaQueryWrapper<>();

        if (currentUser.getRole() == User.ROLE_OWNER) {
            queryWrapper.eq(OwnerContract::getUserId, currentUser.getId());
        } else if (userId != null) {
            queryWrapper.eq(OwnerContract::getUserId, userId);
        }

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(OwnerContract::getContractNo, keyword)
                    .or()
                    .like(OwnerContract::getContractName, keyword)
                    .or()
                    .like(OwnerContract::getRoomInfo, keyword));
        }
        if (status != null) {
            queryWrapper.eq(OwnerContract::getStatus, status);
        }
        if (contractType != null) {
            queryWrapper.eq(OwnerContract::getContractType, contractType);
        }

        queryWrapper.orderByDesc(OwnerContract::getCreateTime);

        Page<OwnerContract> contractPage = ownerContractMapper.selectPage(page, queryWrapper);
        Page<OwnerContractVO> voPage = new Page<>(contractPage.getCurrent(), contractPage.getSize(), contractPage.getTotal());
        voPage.setRecords(contractPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public OwnerContractVO getById(Long id) {
        OwnerContract ownerContract = ownerContractMapper.selectById(id);
        if (ownerContract == null) {
            throw new BusinessException("合同记录不存在");
        }

        validateReadPermission(ownerContract);
        return convertToVO(ownerContract);
    }

    @Override
    public void delete(Long id) {
        OwnerContract ownerContract = ownerContractMapper.selectById(id);
        if (ownerContract == null) {
            throw new BusinessException("合同记录不存在");
        }
        ownerContractMapper.deleteById(id);
    }

    private void validateRequest(OwnerContractRequest request, Long currentId) {
        User owner = userMapper.selectById(request.getUserId());
        if (owner == null || owner.getRole() == null || owner.getRole() != User.ROLE_OWNER) {
            throw new BusinessException("业主不存在");
        }

        LambdaQueryWrapper<OwnerContract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OwnerContract::getContractNo, request.getContractNo());
        if (currentId != null) {
            queryWrapper.ne(OwnerContract::getId, currentId);
        }
        if (ownerContractMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("合同编号已存在");
        }

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
    }

    private void validateReadPermission(OwnerContract ownerContract) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        if (currentUser.getRole() == User.ROLE_OWNER && !ownerContract.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("无权查看该合同");
        }
    }

    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return (LoginUser) authentication.getPrincipal();
    }

    private OwnerContractVO convertToVO(OwnerContract ownerContract) {
        OwnerContractVO vo = new OwnerContractVO();
        BeanUtils.copyProperties(ownerContract, vo);

        User owner = userMapper.selectById(ownerContract.getUserId());
        if (owner != null) {
            vo.setUserName(StringUtils.hasText(owner.getRealName()) ? owner.getRealName() : owner.getUsername());
            vo.setUserPhone(owner.getPhone());
        }

        if (ownerContract.getCreatorId() != null) {
            User creator = userMapper.selectById(ownerContract.getCreatorId());
            if (creator != null) {
                vo.setCreatorName(StringUtils.hasText(creator.getRealName()) ? creator.getRealName() : creator.getUsername());
            }
        }

        if (ownerContract.getSignDate() != null) {
            vo.setSignDate(ownerContract.getSignDate().toString());
        }
        if (ownerContract.getStartDate() != null) {
            vo.setStartDate(ownerContract.getStartDate().toString());
        }
        if (ownerContract.getEndDate() != null) {
            vo.setEndDate(ownerContract.getEndDate().toString());
        }
        if (ownerContract.getCreateTime() != null) {
            vo.setCreateTime(ownerContract.getCreateTime().format(DATE_TIME_FORMATTER));
        }
        return vo;
    }
}
