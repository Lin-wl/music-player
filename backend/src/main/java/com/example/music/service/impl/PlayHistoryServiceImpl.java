package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.music.common.Result;
import com.example.music.entity.PlayHistory;
import com.example.music.entity.Song;
import com.example.music.entity.User;
import com.example.music.mapper.PlayHistoryMapper;
import com.example.music.mapper.SongMapper;
import com.example.music.mapper.UserMapper;
import com.example.music.service.PlayHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 最近播放业务层实现。
 */
@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    private final PlayHistoryMapper playHistoryMapper;
    private final UserMapper userMapper;
    private final SongMapper songMapper;

    public PlayHistoryServiceImpl(PlayHistoryMapper playHistoryMapper, UserMapper userMapper, SongMapper songMapper) {
        this.playHistoryMapper = playHistoryMapper;
        this.userMapper = userMapper;
        this.songMapper = songMapper;
    }

    @Override
    public Result<Void> addPlayHistory(Long userId, Long songId) {
        if (userId == null || songId == null) {
            return Result.error("userId 和 songId 不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Song song = songMapper.selectById(songId);
        if (song == null) {
            return Result.error("歌曲不存在");
        }

        PlayHistory playHistory = new PlayHistory();
        playHistory.setUserId(userId);
        playHistory.setSongId(songId);
        playHistory.setPlayTime(LocalDateTime.now());
        playHistoryMapper.insert(playHistory);

        return Result.success("播放记录已保存", null);
    }

    @Override
    public Result<List<Song>> listRecentSongsByUserId(Long userId) {
        if (userId == null) {
            return Result.error("userId 不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        List<PlayHistory> histories = playHistoryMapper.selectList(
                new LambdaQueryWrapper<PlayHistory>()
                        .eq(PlayHistory::getUserId, userId)
                        .orderByDesc(PlayHistory::getPlayTime)
        );

        if (histories.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> songIds = histories.stream()
                .map(PlayHistory::getSongId)
                .toList();

        Map<Long, Song> songMap = songMapper.selectBatchIds(songIds).stream()
                .collect(Collectors.toMap(Song::getId, Function.identity()));

        List<Song> songs = songIds.stream()
                .map(songMap::get)
                .filter(song -> song != null)
                .collect(Collectors.toList());

        return Result.success(songs);
    }
}
