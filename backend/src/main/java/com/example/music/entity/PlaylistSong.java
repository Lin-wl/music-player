package com.example.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * PlaylistSong 实体类，对应 playlist_song 表。
 */
@Data
@TableName("playlist_song")
public class PlaylistSong {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("playlist_id")
    private Long playlistId;

    @TableField("song_id")
    private Long songId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
