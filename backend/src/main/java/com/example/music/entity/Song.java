package com.example.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Song 实体类，对应数据库中的 song 表。
 */
@Data
@TableName("song")
public class Song {

    /**
     * 歌曲主键 ID。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 歌曲名称。
     */
    private String name;

    /**
     * 歌手名称。
     */
    private String singer;

    /**
     * 专辑名称。
     */
    private String album;

    /**
     * 歌曲播放地址，可以是本地静态资源路径。
     */
    private String url;

    /**
     * 歌曲封面图地址。
     */
    private String cover;

    /**
     * 歌曲时长，单位可自行约定为秒。
     */
    private Integer duration;

    /**
     * 歌词内容。
     */
    private String lyric;

    /**
     * 创建时间，对应数据库字段 create_time。
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
