package com.example.music.controller;

import com.example.music.common.Result;
import com.example.music.entity.Song;
import com.example.music.service.PlayHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 最近播放接口控制层。
 */
@RestController
@RequestMapping("/play-history")
public class PlayHistoryController {

    private final PlayHistoryService playHistoryService;

    public PlayHistoryController(PlayHistoryService playHistoryService) {
        this.playHistoryService = playHistoryService;
    }

    /**
     * 新增一条播放记录。
     */
    @PostMapping
    public Result<Void> addPlayHistory(@RequestParam Long userId, @RequestParam Long songId) {
        return playHistoryService.addPlayHistory(userId, songId);
    }

    /**
     * 查询用户最近播放歌曲列表。
     */
    @GetMapping("/user/{userId}")
    public Result<List<Song>> listRecentSongsByUserId(@PathVariable Long userId) {
        return playHistoryService.listRecentSongsByUserId(userId);
    }
}
