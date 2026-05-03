package com.property.security;

import com.property.common.BusinessException;
import com.property.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * 安全上下文工具类
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return (LoginUser) authentication.getPrincipal();
    }

    public static User getCurrentUser() {
        return getCurrentLoginUser().getUser();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static boolean isManager(User user) {
        return Objects.equals(user.getRole(), User.ROLE_ADMIN)
                || Objects.equals(user.getRole(), User.ROLE_STAFF);
    }
}
