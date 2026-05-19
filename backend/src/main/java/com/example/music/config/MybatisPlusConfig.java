package com.example.music.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 基础配置。
 * 这里主要负责扫描 Mapper 接口，避免每个 Mapper 单独添加注解。
 */
@Configuration
@MapperScan("com.example.music.mapper")
public class MybatisPlusConfig {
}
