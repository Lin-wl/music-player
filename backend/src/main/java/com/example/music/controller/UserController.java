package com.example.music.controller;

import com.example.music.common.Result;
import com.example.music.dto.UserLoginRequest;
import com.example.music.dto.UserRegisterRequest;
import com.example.music.dto.UserResponse;
import com.example.music.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口控制层。
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册。
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@RequestBody UserRegisterRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }

        if (userService.existsByUsername(request.getUsername())) {
            return Result.error("用户名已存在");
        }

        UserResponse userResponse = userService.register(request);
        return userResponse != null ? Result.success("注册成功", userResponse) : Result.error("注册失败");
    }

    /**
     * 用户登录。
     */
    @PostMapping("/login")
    public Result<UserResponse> login(@RequestBody UserLoginRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }

        UserResponse userResponse = userService.login(request);
        return userResponse != null ? Result.success("登录成功", userResponse) : Result.error("用户名或密码错误");
    }
}
