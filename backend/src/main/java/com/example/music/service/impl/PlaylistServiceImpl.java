package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.music.common.Result;
import com.example.music.dto.PlaylistCreateRequest;
import com.example.music.entity.Playlist;
import com.example.music.entity.PlaylistSong;
import com.example.music.entity.Song;
import com.example.music.entity.User;
import com.example.music.mapper.PlaylistMapper;
import com.example.music.mapper.PlaylistSongMapper;
import com.example.music.mapper.SongMapper;
import com.example.music.mapper.UserMapper;
import com.example.music.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 歌单业务层实现。
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final String DEFAULT_COVER = "/cover/default.jpg";

    private final PlaylistMapper playlistMapper;
    private final PlaylistSongMapper playlistSongMapper;
    private final UserMapper userMapper;
    private final SongMapper songMapper;

    public PlaylistServiceImpl(
            PlaylistMapper playlistMapper,
            PlaylistSongMapper playlistSongMapper,
            UserMapper userMapper,
            SongMapper songMapper
    ) {
        this.playlistMapper = playlistMapper;
        this.playlistSongMapper = playlistSongMapper;
        this.userMapper = userMapper;
        this.songMapper = songMapper;
    }

    @Override
    public Result<Playlist> createPlaylist(PlaylistCreateRequest request) {
        if (request == null || request.getUserId() == null || !StringUtils.hasText(request.getName())) {
            return Result.error("userId 和歌单名称不能为空");
        }

        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        Playlist playlist = new Playlist();
        playlist.setUserId(request.getUserId());
        playlist.setName(request.getName().trim());
        playlist.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : "");
        playlist.setCover(StringUtils.hasText(request.getCover()) ? request.getCover().trim() : DEFAULT_COVER);
        playlist.setCreateTime(LocalDateTime.now());
        playlistMapper.insert(playlist);

        return Result.success("创建歌单成功", playlist);
    }

    @Override
    public Result<List<Playlist>> listUserPlaylists(Long userId) {
        if (userId == null) {
            return Result.error("userId 不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        List<Playlist> playlists = playlistMapper.selectList(
                new LambdaQueryWrapper<Playlist>()
                        .eq(Playlist::getUserId, userId)
                        .orderByDesc(Playlist::getCreateTime)
        );
        return Result.success(playlists);
    }

    @Override
    public Result<Void> addSongToPlaylist(Long playlistId, Long songId) {
        if (playlistId == null || songId == null) {
            return Result.error("playlistId 和 songId 不能为空");
        }

        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist == null) {
            return Result.error("歌单不存在");
        }

        Song song = songMapper.selectById(songId);
        if (song == null) {
            return Result.error("歌曲不存在");
        }

        PlaylistSong existing = playlistSongMapper.selectOne(
                new LambdaQueryWrapper<PlaylistSong>()
                        .eq(PlaylistSong::getPlaylistId, playlistId)
                        .eq(PlaylistSong::getSongId, songId)
        );
        if (existing != null) {
            return Result.error("该歌曲已存在于歌单中");
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylistId(playlistId);
        playlistSong.setSongId(songId);
        playlistSong.setCreateTime(LocalDateTime.now());
        playlistSongMapper.insert(playlistSong);

        return Result.success("添加到歌单成功", null);
    }

    @Override
    public Result<Void> removeSongFromPlaylist(Long playlistId, Long songId) {
        if (playlistId == null || songId == null) {
            return Result.error("playlistId 和 songId 不能为空");
        }

        PlaylistSong existing = playlistSongMapper.selectOne(
                new LambdaQueryWrapper<PlaylistSong>()
                        .eq(PlaylistSong::getPlaylistId, playlistId)
                        .eq(PlaylistSong::getSongId, songId)
        );
        if (existing == null) {
            return Result.error("该歌曲不在当前歌单中");
        }

        playlistSongMapper.deleteById(existing.getId());
        return Result.success("已从歌单移除歌曲", null);
    }

    @Override
    public Result<List<Song>> listSongsByPlaylistId(Long playlistId) {
        if (playlistId == null) {
            return Result.error("playlistId 不能为空");
        }

        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist == null) {
            return Result.error("歌单不存在");
        }

        List<PlaylistSong> playlistSongs = playlistSongMapper.selectList(
                new LambdaQueryWrapper<PlaylistSong>()
                        .eq(PlaylistSong::getPlaylistId, playlistId)
                        .orderByDesc(PlaylistSong::getCreateTime)
        );

        if (playlistSongs.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> songIds = playlistSongs.stream()
                .map(PlaylistSong::getSongId)
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
