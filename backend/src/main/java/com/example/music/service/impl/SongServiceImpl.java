package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.music.entity.Song;
import com.example.music.mapper.SongMapper;
import com.example.music.service.SongService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Song 业务层实现类。
 */
@Service
public class SongServiceImpl implements SongService {

    private final SongMapper songMapper;

    public SongServiceImpl(SongMapper songMapper) {
        this.songMapper = songMapper;
    }

    @Override
    public List<Song> listAllSongs() {
        return songMapper.selectList(
                new LambdaQueryWrapper<Song>().orderByDesc(Song::getCreateTime)
        );
    }

    @Override
    public Song getSongById(Long id) {
        return songMapper.selectById(id);
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        LambdaQueryWrapper<Song> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词为空时，直接返回全部歌曲，避免前端传空值时报错。
        if (!StringUtils.hasText(keyword)) {
            return listAllSongs();
        }

        queryWrapper.like(Song::getName, keyword)
                .or()
                .like(Song::getSinger, keyword)
                .or()
                .like(Song::getAlbum, keyword)
                .orderByDesc(Song::getCreateTime);

        return songMapper.selectList(queryWrapper);
    }

    @Override
    public Song createSong(Song song) {
        // 新增时通常不由前端传入主键，避免误把已有记录当成新数据。
        song.setId(null);

        if (song.getCreateTime() == null) {
            song.setCreateTime(LocalDateTime.now());
        }

        songMapper.insert(song);
        return song;
    }

    @Override
    public Song updateSong(Long id, Song song) {
        Song existingSong = songMapper.selectById(id);
        if (existingSong == null) {
            return null;
        }

        // 以路径参数中的 id 为准，避免前端传错主键。
        song.setId(id);

        // 保留原创建时间，避免更新时被意外改掉。
        if (song.getCreateTime() == null) {
            song.setCreateTime(existingSong.getCreateTime());
        }

        songMapper.updateById(song);
        return songMapper.selectById(id);
    }

    @Override
    public boolean deleteSongById(Long id) {
        Song existingSong = songMapper.selectById(id);
        if (existingSong == null) {
            return false;
        }

        return songMapper.deleteById(id) > 0;
    }
}
