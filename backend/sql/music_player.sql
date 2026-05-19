DROP DATABASE IF EXISTS music_player;
CREATE DATABASE music_player
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE music_player;
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT '/cover/default.jpg' COMMENT '头像',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='用户表';

CREATE TABLE singer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '歌手ID',
    name VARCHAR(100) NOT NULL COMMENT '歌手名称',
    gender VARCHAR(20) COMMENT '性别',
    description TEXT COMMENT '歌手简介',
    cover VARCHAR(255) DEFAULT '/cover/default.jpg' COMMENT '歌手封面',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='歌手表';

CREATE TABLE album (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '专辑ID',
    name VARCHAR(100) NOT NULL COMMENT '专辑名称',
    singer_id BIGINT COMMENT '歌手ID',
    description TEXT COMMENT '专辑简介',
    cover VARCHAR(255) DEFAULT '/cover/default.jpg' COMMENT '专辑封面',
    release_time DATE COMMENT '发行日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_album_singer FOREIGN KEY (singer_id) REFERENCES singer(id)
) COMMENT='专辑表';

CREATE TABLE song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '歌曲ID',
    name VARCHAR(100) NOT NULL COMMENT '歌曲名称',
    singer_id BIGINT COMMENT '歌手ID',
    album_id BIGINT COMMENT '专辑ID',
    singer VARCHAR(100) COMMENT '歌手名称，便于直接展示',
    album VARCHAR(100) COMMENT '专辑名称，便于直接展示',
    url VARCHAR(255) NOT NULL COMMENT '歌曲播放地址',
    cover VARCHAR(255) DEFAULT '/cover/default.jpg' COMMENT '歌曲封面',
    duration INT COMMENT '歌曲时长，单位秒',
    lyric TEXT COMMENT '歌词内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_song_singer FOREIGN KEY (singer_id) REFERENCES singer(id),
    CONSTRAINT fk_song_album FOREIGN KEY (album_id) REFERENCES album(id)
) COMMENT='歌曲表';

CREATE TABLE playlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '歌单ID',
    user_id BIGINT NOT NULL COMMENT '创建者用户ID',
    name VARCHAR(100) NOT NULL COMMENT '歌单名称',
    description TEXT COMMENT '歌单描述',
    cover VARCHAR(255) DEFAULT '/cover/default.jpg' COMMENT '歌单封面',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_playlist_user FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='歌单表';

CREATE TABLE playlist_song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    playlist_id BIGINT NOT NULL COMMENT '歌单ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    CONSTRAINT fk_playlist_song_playlist FOREIGN KEY (playlist_id) REFERENCES playlist(id),
    CONSTRAINT fk_playlist_song_song FOREIGN KEY (song_id) REFERENCES song(id)
) COMMENT='歌单歌曲关联表';

CREATE TABLE favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_favorite_song FOREIGN KEY (song_id) REFERENCES song(id)
) COMMENT='收藏表';

CREATE TABLE play_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '播放记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    play_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    CONSTRAINT fk_play_history_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_play_history_song FOREIGN KEY (song_id) REFERENCES song(id)
) COMMENT='播放记录表';

INSERT INTO user (username, password, nickname, avatar) VALUES
('admin', '123456', '管理员', '/cover/default.jpg'),
('student', '123456', '课程用户', '/cover/default.jpg');

INSERT INTO singer (name, gender, description, cover) VALUES
('周杰伦', '男', '华语流行男歌手，作品风格多样。', '/cover/default.jpg'),
('林俊杰', '男', '华语流行男歌手，擅长抒情与流行曲风。', '/cover/default.jpg'),
('孙燕姿', '女', '华语流行女歌手，代表作众多。', '/cover/default.jpg');

INSERT INTO album (name, singer_id, description, cover, release_time) VALUES
('魔杰座', 1, '周杰伦经典专辑之一。', '/cover/default.jpg', '2008-10-14'),
('十一月的萧邦', 1, '周杰伦代表性专辑。', '/cover/default.jpg', '2005-11-01'),
('曹操', 2, '林俊杰经典专辑。', '/cover/default.jpg', '2006-02-17'),
('Stefanie', 3, '孙燕姿早期专辑。', '/cover/default.jpg', '2004-10-28');

INSERT INTO song (name, singer_id, album_id, singer, album, url, cover, duration, lyric) VALUES
('稻香', 1, 1, '周杰伦', '魔杰座', '/music/song1.mp3', '/cover/default.jpg', 223, '对这个世界如果你有太多的抱怨'),
('夜曲', 1, 2, '周杰伦', '十一月的萧邦', '/music/song2.mp3', '/cover/default.jpg', 234, '为你弹奏肖邦的夜曲'),
('晴天', 1, 2, '周杰伦', '十一月的萧邦', '/music/song3.mp3', '/cover/default.jpg', 269, '故事的小黄花'),
('曹操', 2, 3, '林俊杰', '曹操', '/music/song4.mp3', '/cover/default.jpg', 215, '不是英雄 不读三国'),
('遇见', 3, 4, '孙燕姿', 'Stefanie', '/music/song5.mp3', '/cover/default.jpg', 203, '听见冬天的离开');

INSERT INTO playlist (user_id, name, description, cover) VALUES
(1, '默认歌单', '系统初始化歌单', '/cover/default.jpg'),
(2, '我的最爱', '课程作业测试歌单', '/cover/default.jpg');

INSERT INTO playlist_song (playlist_id, song_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 5);

INSERT INTO favorite (user_id, song_id) VALUES
(1, 1),
(1, 2),
(2, 5);

INSERT INTO play_history (user_id, song_id, play_time) VALUES
(1, 1, NOW()),
(1, 3, NOW()),
(2, 5, NOW());
