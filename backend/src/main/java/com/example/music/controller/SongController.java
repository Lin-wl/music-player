package com.example.music.controller;

import com.example.music.common.Result;
import com.example.music.entity.Song;
import com.example.music.service.SongService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Song 控制层，负责接收前端请求并返回数据。
 */
@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    /**
     * 查询全部歌曲。
     */
    @GetMapping
    public Result<List<Song>> listSongs() {
        return Result.success(songService.listAllSongs());
    }

    /**
     * 根据 id 查询歌曲详情。
     */
    @GetMapping("/{id}")
    public Result<Song> getSongById(@PathVariable Long id) {
        Song song = songService.getSongById(id);
        return song != null ? Result.success(song) : Result.error("歌曲不存在");
    }

    /**
     * 根据关键词搜索歌曲。
     */
    @GetMapping("/search")
    public Result<List<Song>> searchSongs(@RequestParam(required = false) String keyword) {
        return Result.success(songService.searchSongs(keyword));
    }

    /**
     * 新增歌曲。
     */
    @PostMapping
    public Result<Song> createSong(@RequestBody Song song) {
        return Result.success("新增歌曲成功", songService.createSong(song));
    }

    /**
     * 修改歌曲。
     */
    @PutMapping("/{id}")
    public Result<Song> updateSong(@PathVariable Long id, @RequestBody Song song) {
        Song updatedSong = songService.updateSong(id, song);
        return updatedSong != null ? Result.success("修改歌曲成功", updatedSong) : Result.error("歌曲不存在");
    }

    /**
     * 删除歌曲。
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSong(@PathVariable Long id) {
        return songService.deleteSongById(id) ? Result.success("删除歌曲成功", null) : Result.error("歌曲不存在");
    }
}
