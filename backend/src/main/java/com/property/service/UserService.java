package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.UserRequest;
import com.property.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 新增用户
     */
    UserVO create(UserRequest request);
    
    /**
     * 更新用户
     */
    UserVO update(Long id, UserRequest request);
    
    /**
     * 分页查询用户列表
     */
    Page<UserVO> getPage(Integer pageNum, Integer pageSize, String username, Integer role);
    
    /**
     * 获取用户详情
     */
    UserVO getById(Long id);
    
    /**
     * 删除用户
     */
    void delete(Long id);
    
    /**
     * 更新用户状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 获取业主列表
     */
    List<UserVO> getOwnerList();
}
