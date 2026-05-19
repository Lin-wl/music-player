package com.example.music.service;

import com.example.music.common.Result;
import com.example.music.entity.Song;

import java.util.List;

/**
 * 最近播放业务层接口。
 */
public interface PlayHistoryService {

    Result<Void> addPlayHistory(Long userId, Long songId);

    Result<List<Song>> listRecentSongsByUserId(Long userId);
}
