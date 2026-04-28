package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.dto.VoteActivityRequest;
import com.property.dto.VoteSubmitRequest;
import com.property.entity.User;
import com.property.entity.VoteActivity;
import com.property.entity.VoteResponse;
import com.property.mapper.UserMapper;
import com.property.mapper.VoteActivityMapper;
import com.property.mapper.VoteResponseMapper;
import com.property.security.LoginUser;
import com.property.service.VoteService;
import com.property.vo.VoteActivityVO;
import com.property.vo.VoteOptionStatVO;
import com.property.vo.VoteResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 投票问卷服务实现
 */
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final TypeReference<List<String>> OPTION_TYPE = new TypeReference<>() {};

    private final VoteActivityMapper voteActivityMapper;
    private final VoteResponseMapper voteResponseMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public VoteActivityVO create(VoteActivityRequest request) {
        LoginUser loginUser = getCurrentLoginUser();
        validateManager(loginUser.getUser());
        validateActivityRequest(request);

        VoteActivity activity = new VoteActivity();
        activity.setTitle(request.getTitle().trim());
        activity.setDescription(trimToNull(request.getDescription()));
        activity.setType(request.getType());
        activity.setStatus(VoteActivity.STATUS_OPEN);
        activity.setOptions(toOptionsJson(normalizeOptions(request.getOptions(), request.getType())));
        activity.setPublisherId(loginUser.getUser().getId());
        activity.setEndTime(request.getEndTime());
        activity.setDeleted(0);
        voteActivityMapper.insert(activity);

        return buildVO(activity, loginUser.getUser(), false);
    }

    @Override
    public Page<VoteActivityVO> getPage(Integer pageNum, Integer pageSize, Integer type, Integer status, String title) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();

        Page<VoteActivity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<VoteActivity> queryWrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            queryWrapper.eq(VoteActivity::getType, type);
        }
        if (StringUtils.hasText(title)) {
            queryWrapper.like(VoteActivity::getTitle, title.trim());
        }
        applyStatusFilter(queryWrapper, status);
        queryWrapper.orderByDesc(VoteActivity::getCreateTime);

        Page<VoteActivity> activityPage = voteActivityMapper.selectPage(page, queryWrapper);
        Page<VoteActivityVO> voPage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        List<VoteActivityVO> records = activityPage.getRecords().stream()
                .map(activity -> buildVO(activity, currentUser, false))
                .toList();
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    public VoteActivityVO getById(Long id) {
        LoginUser loginUser = getCurrentLoginUser();
        VoteActivity activity = getActivityOrThrow(id);
        return buildVO(activity, loginUser.getUser(), true);
    }

    @Override
    public void submit(Long id, VoteSubmitRequest request) {
        LoginUser loginUser = getCurrentLoginUser();
        User currentUser = loginUser.getUser();
        if (!Objects.equals(currentUser.getRole(), User.ROLE_OWNER)) {
            throw new BusinessException("仅业主可以参与投票/问卷");
        }

        VoteActivity activity = getActivityOrThrow(id);
        if (resolveStatus(activity) == VoteActivity.STATUS_CLOSED) {
            throw new BusinessException("当前活动已结束，无法参与");
        }

        VoteResponse existed = getResponseByActivityAndUser(id, currentUser.getId());
        if (existed != null) {
            throw new BusinessException("您已参与过该活动");
        }

        VoteResponse response = new VoteResponse();
        response.setActivityId(id);
        response.setUserId(currentUser.getId());

        if (Objects.equals(activity.getType(), VoteActivity.TYPE_VOTE)) {
            List<String> options = parseOptions(activity.getOptions());
            String selectedOption = trimToNull(request.getSelectedOption());
            if (!StringUtils.hasText(selectedOption)) {
                throw new BusinessException("请选择投票选项");
            }
            if (!options.contains(selectedOption)) {
                throw new BusinessException("投票选项无效");
            }
            response.setSelectedOption(selectedOption);
        } else {
            String content = trimToNull(request.getContent());
            if (!StringUtils.hasText(content)) {
                throw new BusinessException("请填写征集意见内容");
            }
            response.setContent(content);
        }

        response.setDeleted(0);
        voteResponseMapper.insert(response);
    }

    @Override
    public void close(Long id) {
        LoginUser loginUser = getCurrentLoginUser();
        validateManager(loginUser.getUser());

        VoteActivity activity = getActivityOrThrow(id);
        if (Objects.equals(resolveStatus(activity), VoteActivity.STATUS_CLOSED)
                && Objects.equals(activity.getStatus(), VoteActivity.STATUS_CLOSED)) {
            throw new BusinessException("该活动已结束");
        }

        activity.setStatus(VoteActivity.STATUS_CLOSED);
        voteActivityMapper.updateById(activity);
    }

    @Override
    public void delete(Long id) {
        LoginUser loginUser = getCurrentLoginUser();
        validateManager(loginUser.getUser());
        VoteActivity activity = getActivityOrThrow(id);
        LambdaQueryWrapper<VoteResponse> responseQueryWrapper = new LambdaQueryWrapper<>();
        responseQueryWrapper.eq(VoteResponse::getActivityId, activity.getId());
        voteResponseMapper.delete(responseQueryWrapper);
        voteActivityMapper.deleteById(activity.getId());
    }

    private VoteActivityVO buildVO(VoteActivity activity, User currentUser, boolean withDetails) {
        VoteActivityVO vo = new VoteActivityVO();
        BeanUtils.copyProperties(activity, vo);
        vo.setOptions(parseOptions(activity.getOptions()));
        vo.setStatus(resolveStatus(activity));
        vo.setCreateTime(formatTime(activity.getCreateTime()));
        vo.setEndTime(formatTime(activity.getEndTime()));
        vo.setPublisherName(getUserName(activity.getPublisherId()));

        List<VoteResponse> responses = getResponsesByActivity(activity.getId());
        vo.setResponseCount((long) responses.size());

        VoteResponse myResponse = getResponseByActivityAndUser(activity.getId(), currentUser.getId());
        vo.setParticipated(myResponse != null);
        if (myResponse != null) {
            vo.setMySelectedOption(myResponse.getSelectedOption());
            vo.setMyContent(myResponse.getContent());
        }

        if (!withDetails) {
            return vo;
        }

        if (Objects.equals(activity.getType(), VoteActivity.TYPE_VOTE)) {
            boolean canSeeStats = !Objects.equals(currentUser.getRole(), User.ROLE_OWNER)
                    || vo.getParticipated()
                    || Objects.equals(vo.getStatus(), VoteActivity.STATUS_CLOSED);
            if (canSeeStats) {
                vo.setOptionStats(buildOptionStats(vo.getOptions(), responses));
            }
        }

        if (!Objects.equals(currentUser.getRole(), User.ROLE_OWNER)) {
            vo.setResponses(responses.stream().map(this::convertResponseVO).toList());
        }

        return vo;
    }

    private List<VoteOptionStatVO> buildOptionStats(List<String> options, List<VoteResponse> responses) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Long> counter = new LinkedHashMap<>();
        for (String option : options) {
            counter.put(option, 0L);
        }
        for (VoteResponse response : responses) {
            if (StringUtils.hasText(response.getSelectedOption()) && counter.containsKey(response.getSelectedOption())) {
                counter.put(response.getSelectedOption(), counter.get(response.getSelectedOption()) + 1);
            }
        }

        long total = responses.stream().filter(item -> StringUtils.hasText(item.getSelectedOption())).count();
        List<VoteOptionStatVO> stats = new ArrayList<>();
        for (Map.Entry<String, Long> entry : counter.entrySet()) {
            VoteOptionStatVO stat = new VoteOptionStatVO();
            stat.setOptionName(entry.getKey());
            stat.setCount(entry.getValue());
            double percentage = total == 0 ? 0D : entry.getValue() * 100D / total;
            stat.setPercentage(Math.round(percentage * 100D) / 100D);
            stats.add(stat);
        }
        return stats;
    }

    private VoteResponseVO convertResponseVO(VoteResponse response) {
        VoteResponseVO vo = new VoteResponseVO();
        BeanUtils.copyProperties(response, vo);
        vo.setUserName(getUserName(response.getUserId()));
        vo.setCreateTime(formatTime(response.getCreateTime()));
        return vo;
    }

    private List<VoteResponse> getResponsesByActivity(Long activityId) {
        LambdaQueryWrapper<VoteResponse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoteResponse::getActivityId, activityId);
        queryWrapper.orderByAsc(VoteResponse::getCreateTime);
        return voteResponseMapper.selectList(queryWrapper);
    }

    private VoteResponse getResponseByActivityAndUser(Long activityId, Long userId) {
        LambdaQueryWrapper<VoteResponse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoteResponse::getActivityId, activityId);
        queryWrapper.eq(VoteResponse::getUserId, userId);
        return voteResponseMapper.selectOne(queryWrapper);
    }

    private VoteActivity getActivityOrThrow(Long id) {
        VoteActivity activity = voteActivityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("投票/问卷活动不存在");
        }
        return activity;
    }

    private void validateActivityRequest(VoteActivityRequest request) {
        if (!Objects.equals(request.getType(), VoteActivity.TYPE_VOTE)
                && !Objects.equals(request.getType(), VoteActivity.TYPE_SURVEY)) {
            throw new BusinessException("活动类型不正确");
        }
        if (request.getEndTime() != null && request.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("结束时间不能早于当前时间");
        }

        List<String> options = normalizeOptions(request.getOptions(), request.getType());
        if (Objects.equals(request.getType(), VoteActivity.TYPE_VOTE) && options.size() < 2) {
            throw new BusinessException("业主投票至少需要两个选项");
        }
    }

    private List<String> normalizeOptions(List<String> options, Integer type) {
        if (!Objects.equals(type, VoteActivity.TYPE_VOTE)) {
            return Collections.emptyList();
        }
        if (options == null) {
            return Collections.emptyList();
        }
        return options.stream()
                .map(this::trimToNull)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private String toOptionsJson(List<String> options) {
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            throw new BusinessException("投票选项保存失败");
        }
    }

    private List<String> parseOptions(String optionsJson) {
        if (!StringUtils.hasText(optionsJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(optionsJson, OPTION_TYPE);
        } catch (JsonProcessingException e) {
            throw new BusinessException("投票选项数据异常");
        }
    }

    private void applyStatusFilter(LambdaQueryWrapper<VoteActivity> queryWrapper, Integer status) {
        if (status == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(status, VoteActivity.STATUS_OPEN)) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(VoteActivity::getStatus, VoteActivity.STATUS_OPEN)
                    .and(timeWrapper -> timeWrapper.isNull(VoteActivity::getEndTime)
                            .or()
                            .ge(VoteActivity::getEndTime, now)));
        } else if (Objects.equals(status, VoteActivity.STATUS_CLOSED)) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(VoteActivity::getStatus, VoteActivity.STATUS_CLOSED)
                    .or()
                    .lt(VoteActivity::getEndTime, now));
        }
    }

    private Integer resolveStatus(VoteActivity activity) {
        if (Objects.equals(activity.getStatus(), VoteActivity.STATUS_CLOSED)) {
            return VoteActivity.STATUS_CLOSED;
        }
        if (activity.getEndTime() != null && activity.getEndTime().isBefore(LocalDateTime.now())) {
            return VoteActivity.STATUS_CLOSED;
        }
        return VoteActivity.STATUS_OPEN;
    }

    private void validateManager(User user) {
        if (!Objects.equals(user.getRole(), User.ROLE_ADMIN) && !Objects.equals(user.getRole(), User.ROLE_STAFF)) {
            throw new BusinessException("无权执行该操作");
        }
    }

    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return (LoginUser) authentication.getPrincipal();
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
}
