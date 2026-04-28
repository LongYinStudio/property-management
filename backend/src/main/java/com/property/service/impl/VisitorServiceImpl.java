package com.property.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.VisitorRecordRequest;
import com.property.entity.User;
import com.property.entity.VisitorRecord;
import com.property.mapper.UserMapper;
import com.property.mapper.VisitorRecordMapper;
import com.property.security.LoginUser;
import com.property.service.VisitorService;
import com.property.vo.VisitorRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 访客服务实现
 */
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VisitorRecordMapper visitorRecordMapper;
    private final UserMapper userMapper;

    @Override
    public VisitorRecordVO create(VisitorRecordRequest request) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        if (!Objects.equals(currentUser.getRole(), User.ROLE_OWNER)) {
            throw new BusinessException("仅业主可以登记访客");
        }

        validateCreateRequest(request);

        VisitorRecord record = new VisitorRecord();
        record.setOwnerId(currentUser.getId());
        record.setVisitorName(request.getVisitorName().trim());
        record.setVisitorPhone(request.getVisitorPhone().trim());
        record.setVisitorCount(request.getVisitorCount());
        record.setLicensePlate(trimToNull(request.getLicensePlate()));
        record.setPurpose(request.getPurpose().trim());
        record.setVisitTime(request.getVisitTime());
        record.setValidUntil(request.getValidUntil() != null ? request.getValidUntil() : request.getVisitTime().plusHours(6));
        record.setPassCode(generatePassCode());
        record.setStatus(VisitorRecord.STATUS_PENDING);
        record.setRemark(trimToNull(request.getRemark()));
        record.setDeleted(0);
        visitorRecordMapper.insert(record);

        return convertToVO(record);
    }

    @Override
    public Page<VisitorRecordVO> getPage(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        expireOverdueRecords();

        Page<VisitorRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<VisitorRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.equals(currentUser.getRole(), User.ROLE_OWNER)) {
            queryWrapper.eq(VisitorRecord::getOwnerId, currentUser.getId());
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(VisitorRecord::getVisitorName, keyword.trim())
                    .or()
                    .like(VisitorRecord::getPassCode, keyword.trim())
                    .or()
                    .like(VisitorRecord::getVisitorPhone, keyword.trim()));
        }
        applyStatusFilter(queryWrapper, status);
        queryWrapper.orderByDesc(VisitorRecord::getCreateTime);

        Page<VisitorRecord> recordPage = visitorRecordMapper.selectPage(page, queryWrapper);
        Page<VisitorRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(recordPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    public VisitorRecordVO getById(Long id) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        expireOverdueRecords();

        VisitorRecord record = getRecordOrThrow(id);
        validateAccess(currentUser, record);
        return convertToVO(record);
    }

    @Override
    public void verify(Long id) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        if (!isManager(currentUser)) {
            throw new BusinessException("无权核销通行证");
        }
        expireOverdueRecords();

        VisitorRecord record = getRecordOrThrow(id);
        if (!Objects.equals(record.getStatus(), VisitorRecord.STATUS_PENDING)) {
            throw new BusinessException("当前通行证状态不可核销");
        }

        record.setStatus(VisitorRecord.STATUS_PASSED);
        record.setVerifierId(currentUser.getId());
        record.setVerifyTime(LocalDateTime.now());
        visitorRecordMapper.updateById(record);
    }

    @Override
    public void cancel(Long id) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        expireOverdueRecords();

        VisitorRecord record = getRecordOrThrow(id);
        validateAccess(currentUser, record);

        if (Objects.equals(record.getStatus(), VisitorRecord.STATUS_PASSED)) {
            throw new BusinessException("访客已通行，无法取消");
        }
        if (Objects.equals(record.getStatus(), VisitorRecord.STATUS_EXPIRED)) {
            throw new BusinessException("通行证已失效，无法取消");
        }
        if (Objects.equals(record.getStatus(), VisitorRecord.STATUS_CANCELED)) {
            throw new BusinessException("该登记已取消");
        }

        if (Objects.equals(currentUser.getRole(), User.ROLE_OWNER) && !Objects.equals(record.getOwnerId(), currentUser.getId())) {
            throw new BusinessException("无权取消该访客登记");
        }

        record.setStatus(VisitorRecord.STATUS_CANCELED);
        visitorRecordMapper.updateById(record);
    }

    private void validateCreateRequest(VisitorRecordRequest request) {
        if (request.getVisitTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new BusinessException("到访时间不能早于当前时间");
        }

        LocalDateTime validUntil = request.getValidUntil() != null ? request.getValidUntil() : request.getVisitTime().plusHours(6);
        if (validUntil.isBefore(request.getVisitTime())) {
            throw new BusinessException("有效截止时间不能早于到访时间");
        }
    }

    private String generatePassCode() {
        for (int i = 0; i < 10; i++) {
            String code = RandomUtil.randomStringUpper(8);
            LambdaQueryWrapper<VisitorRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VisitorRecord::getPassCode, code);
            if (visitorRecordMapper.selectCount(queryWrapper) == 0) {
                return code;
            }
        }
        throw new BusinessException("通行证生成失败，请稍后重试");
    }

    private VisitorRecord getRecordOrThrow(Long id) {
        VisitorRecord record = visitorRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("访客登记不存在");
        }
        return record;
    }

    private void validateAccess(User currentUser, VisitorRecord record) {
        if (isManager(currentUser)) {
            return;
        }
        if (!Objects.equals(record.getOwnerId(), currentUser.getId())) {
            throw new BusinessException("无权查看该访客登记");
        }
    }

    private boolean isManager(User user) {
        return Objects.equals(user.getRole(), User.ROLE_ADMIN) || Objects.equals(user.getRole(), User.ROLE_STAFF);
    }

    private void applyStatusFilter(LambdaQueryWrapper<VisitorRecord> queryWrapper, Integer status) {
        if (status == null) {
            return;
        }
        queryWrapper.eq(VisitorRecord::getStatus, status);
    }

    private void expireOverdueRecords() {
        LambdaQueryWrapper<VisitorRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VisitorRecord::getStatus, VisitorRecord.STATUS_PENDING);
        queryWrapper.lt(VisitorRecord::getValidUntil, LocalDateTime.now());
        visitorRecordMapper.selectList(queryWrapper).forEach(record -> {
            record.setStatus(VisitorRecord.STATUS_EXPIRED);
            visitorRecordMapper.updateById(record);
        });
    }

    private VisitorRecordVO convertToVO(VisitorRecord record) {
        VisitorRecordVO vo = new VisitorRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setOwnerName(getUserName(record.getOwnerId()));
        vo.setVerifierName(getUserName(record.getVerifierId()));
        vo.setVisitTime(formatTime(record.getVisitTime()));
        vo.setValidUntil(formatTime(record.getValidUntil()));
        vo.setVerifyTime(formatTime(record.getVerifyTime()));
        vo.setCreateTime(formatTime(record.getCreateTime()));
        return vo;
    }

    private String getUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return StringUtils.hasText(user.getRealName()) ? user.getRealName() : user.getUsername();
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? null : time.format(FORMATTER);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return (LoginUser) authentication.getPrincipal();
    }
}
