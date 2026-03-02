package com.property.security;

import com.property.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    private User user;
    
    public LoginUser(User user) {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = switch (user.getRole()) {
            case 1 -> "ADMIN";
            case 2 -> "STAFF";
            case 3 -> "OWNER";
            default -> "USER";
        };
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return User.STATUS_ENABLE == user.getStatus();
    }
}
