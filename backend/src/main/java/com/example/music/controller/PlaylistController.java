package com.example.music.controller;

import com.example.music.common.Result;
import com.example.music.dto.PlaylistCreateRequest;
import com.example.music.entity.Playlist;
import com.example.music.entity.Song;
import com.example.music.service.PlaylistService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 歌单接口控制层。
 */
@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public Result<Playlist> createPlaylist(@RequestBody PlaylistCreateRequest request) {
        return playlistService.createPlaylist(request);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Playlist>> listUserPlaylists(@PathVariable Long userId) {
        return playlistService.listUserPlaylists(userId);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public Result<Void> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        return playlistService.addSongToPlaylist(playlistId, songId);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public Result<Void> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        return playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    @GetMapping("/{playlistId}/songs")
    public Result<List<Song>> listSongsByPlaylistId(@PathVariable Long playlistId) {
        return playlistService.listSongsByPlaylistId(playlistId);
    }
}
