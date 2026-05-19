package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.music.dto.UserLoginRequest;
import com.example.music.dto.UserRegisterRequest;
import com.example.music.dto.UserResponse;
import com.example.music.entity.User;
import com.example.music.mapper.UserMapper;
import com.example.music.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户业务层实现。
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_AVATAR = "/cover/default.jpg";

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean existsByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username.trim())
        );
        return user != null;
    }

    @Override
    public UserResponse register(UserRegisterRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            return null;
        }

        if (existsByUsername(request.getUsername())) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(request.getPassword().trim());
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname().trim() : request.getUsername().trim());
        user.setAvatar(DEFAULT_AVATAR);
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);
        return toResponse(user);
    }

    @Override
    public UserResponse login(UserLoginRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            return null;
        }

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername().trim())
                        .eq(User::getPassword, request.getPassword().trim())
        );

        return user == null ? null : toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getCreateTime()
        );
    }
}
