package com.example.music.service;

import com.example.music.dto.UserLoginRequest;
import com.example.music.dto.UserRegisterRequest;
import com.example.music.dto.UserResponse;

/**
 * 用户业务层接口。
 */
public interface UserService {

    /**
     * 判断用户名是否已存在。
     *
     * @param username 用户名
     * @return 已存在返回 true
     */
    boolean existsByUsername(String username);

    /**
     * 注册用户。
     *
     * @param request 注册信息
     * @return 注册成功后的用户基本信息；失败返回 null
     */
    UserResponse register(UserRegisterRequest request);

    /**
     * 用户登录。
     *
     * @param request 登录信息
     * @return 登录成功后的用户基本信息；失败返回 null
     */
    UserResponse login(UserLoginRequest request);
}
