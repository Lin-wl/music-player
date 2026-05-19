package com.example.music.controller;

import com.example.music.common.Result;
import com.example.music.entity.Song;
import com.example.music.service.FavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收藏接口控制层。
 */
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏歌曲。
     */
    @PostMapping
    public Result<Void> addFavorite(@RequestParam Long userId, @RequestParam Long songId) {
        return favoriteService.addFavorite(userId, songId);
    }

    /**
     * 取消收藏。
     */
    @DeleteMapping
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long songId) {
        return favoriteService.removeFavorite(userId, songId);
    }

    /**
     * 查询用户收藏歌曲列表。
     */
    @GetMapping("/user/{userId}")
    public Result<List<Song>> listFavoriteSongsByUserId(@PathVariable Long userId) {
        return favoriteService.listFavoriteSongsByUserId(userId);
    }
}
