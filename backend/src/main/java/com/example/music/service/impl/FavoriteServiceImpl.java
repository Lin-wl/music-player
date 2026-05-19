package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.music.common.Result;
import com.example.music.entity.Favorite;
import com.example.music.entity.Song;
import com.example.music.entity.User;
import com.example.music.mapper.FavoriteMapper;
import com.example.music.mapper.SongMapper;
import com.example.music.mapper.UserMapper;
import com.example.music.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 收藏业务层实现。
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final SongMapper songMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, UserMapper userMapper, SongMapper songMapper) {
        this.favoriteMapper = favoriteMapper;
        this.userMapper = userMapper;
        this.songMapper = songMapper;
    }

    @Override
    public Result<Void> addFavorite(Long userId, Long songId) {
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

        Favorite existingFavorite = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getSongId, songId)
        );
        if (existingFavorite != null) {
            return Result.error("该歌曲已收藏，请勿重复收藏");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setSongId(songId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);

        return Result.success("收藏成功", null);
    }

    @Override
    public Result<Void> removeFavorite(Long userId, Long songId) {
        if (userId == null || songId == null) {
            return Result.error("userId 和 songId 不能为空");
        }

        Favorite existingFavorite = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getSongId, songId)
        );
        if (existingFavorite == null) {
            return Result.error("该歌曲尚未收藏");
        }

        favoriteMapper.deleteById(existingFavorite.getId());
        return Result.success("取消收藏成功", null);
    }

    @Override
    public Result<List<Song>> listFavoriteSongsByUserId(Long userId) {
        if (userId == null) {
            return Result.error("userId 不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        List<Favorite> favorites = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime)
        );

        if (favorites.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> songIds = favorites.stream()
                .map(Favorite::getSongId)
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
