CREATE DATABASE IF NOT EXISTS music_player
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE music_player;

CREATE TABLE IF NOT EXISTS song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '歌曲ID',
    name VARCHAR(100) NOT NULL COMMENT '歌曲名称',
    singer VARCHAR(100) COMMENT '歌手名称',
    album VARCHAR(100) COMMENT '专辑名称',
    url VARCHAR(255) COMMENT '歌曲播放地址',
    cover VARCHAR(255) COMMENT '封面图片地址',
    duration INT COMMENT '歌曲时长，单位秒',
    lyric TEXT COMMENT '歌词内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='歌曲表';

INSERT INTO song (name, singer, album, url, cover, duration, lyric)
VALUES
    ('稻香', '周杰伦', '魔杰座', '/music/daoxiang.mp3', '/images/daoxiang.jpg', 223, '还没学会保存歌词时可以先放示例文本'),
    ('夜曲', '周杰伦', '十一月的萧邦', '/music/yequ.mp3', '/images/yequ.jpg', 234, '为你弹奏肖邦的夜曲'),
    ('演员', '薛之谦', '绅士', '/music/yanyuan.mp3', '/images/yanyuan.jpg', 250, '简单点 说话的方式简单点');
