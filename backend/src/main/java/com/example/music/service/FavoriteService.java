package com.example.music.service;

import com.example.music.common.Result;
import com.example.music.entity.Song;

import java.util.List;

/**
 * 收藏业务层接口。
 */
public interface FavoriteService {

    Result<Void> addFavorite(Long userId, Long songId);

    Result<Void> removeFavorite(Long userId, Long songId);

    Result<List<Song>> listFavoriteSongsByUserId(Long userId);
}
