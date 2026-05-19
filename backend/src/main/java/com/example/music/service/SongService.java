package com.example.music.service;

import com.example.music.entity.Song;

import java.util.List;

/**
 * Song 业务层接口。
 */
public interface SongService {

    /**
     * 查询全部歌曲。
     *
     * @return 歌曲列表
     */
    List<Song> listAllSongs();

    /**
     * 根据 ID 查询歌曲详情。
     *
     * @param id 歌曲ID
     * @return 歌曲详情；如果不存在则返回 null
     */
    Song getSongById(Long id);

    /**
     * 根据关键词搜索歌曲，支持匹配歌曲名、歌手名、专辑名。
     *
     * @param keyword 搜索关键词
     * @return 匹配到的歌曲列表
     */
    List<Song> searchSongs(String keyword);

    /**
     * 新增歌曲。
     *
     * @param song 歌曲信息
     * @return 新增后的歌曲对象
     */
    Song createSong(Song song);

    /**
     * 修改歌曲。
     *
     * @param id   歌曲ID
     * @param song 修改后的歌曲信息
     * @return 修改后的歌曲对象；如果不存在则返回 null
     */
    Song updateSong(Long id, Song song);

    /**
     * 删除歌曲。
     *
     * @param id 歌曲ID
     * @return 删除成功返回 true；如果歌曲不存在返回 false
     */
    boolean deleteSongById(Long id);
}
