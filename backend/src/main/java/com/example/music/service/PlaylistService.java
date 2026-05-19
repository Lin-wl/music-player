package com.example.music.service;

import com.example.music.common.Result;
import com.example.music.dto.PlaylistCreateRequest;
import com.example.music.entity.Playlist;
import com.example.music.entity.Song;

import java.util.List;

/**
 * 歌单业务层接口。
 */
public interface PlaylistService {

    Result<Playlist> createPlaylist(PlaylistCreateRequest request);

    Result<List<Playlist>> listUserPlaylists(Long userId);

    Result<Void> addSongToPlaylist(Long playlistId, Long songId);

    Result<Void> removeSongFromPlaylist(Long playlistId, Long songId);

    Result<List<Song>> listSongsByPlaylistId(Long playlistId);
}
