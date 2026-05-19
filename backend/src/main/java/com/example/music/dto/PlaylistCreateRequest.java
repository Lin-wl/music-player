package com.example.music.dto;

import lombok.Data;

/**
 * 创建歌单请求参数。
 */
@Data
public class PlaylistCreateRequest {

    /**
     * 创建者用户ID。
     */
    private Long userId;

    /**
     * 歌单名称。
     */
    private String name;

    /**
     * 歌单描述。
     */
    private String description;

    /**
     * 歌单封面，可为空。
     */
    private String cover;
}
