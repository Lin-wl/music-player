package com.example.music.dto;

import lombok.Data;

/**
 * 用户注册请求参数。
 */
@Data
public class UserRegisterRequest {

    /**
     * 用户名。
     */
    private String username;

    /**
     * 密码。
     */
    private String password;

    /**
     * 昵称，允许为空。
     */
    private String nickname;
}
