<script setup>
import { computed } from "vue";
import SongCard from "./SongCard.vue";

const props = defineProps({
  currentView: { type: String, required: true },
  searchKeyword: { type: String, default: "" },
  songs: { type: Array, default: () => [] },
  favoriteSongs: { type: Array, default: () => [] },
  recentSongs: { type: Array, default: () => [] },
  playlists: { type: Array, default: () => [] },
  playlistSongs: { type: Array, default: () => [] },
  selectedPlaylistId: { type: Number, default: null },
  currentSongId: { type: [Number, String], default: null },
  favoriteIds: { type: Array, default: () => [] },
  currentUser: { type: Object, default: null },
  isLoading: { type: Boolean, default: false },
  errorMessage: { type: String, default: "" }
});

const emit = defineEmits([
  "play",
  "toggle-favorite",
  "open-playlist",
  "select-playlist",
  "remove-playlist-song",
  "open-auth",
  "reload"
]);

const placeholderMap = {
  podcast: ["播客", "这个区域保留给后续播客内容扩展。"],
  video: ["视频", "这里可以在后续版本中接入 MV 或短视频内容。"],
  follow: ["关注", "这里可以扩展成好友关注或音乐社区动态。"],
  local: ["本地与下载", "本地资源已通过后端静态映射提供，这里先保留入口视图。"]
};

const recommendedSongs = computed(() => props.songs.slice(0, 6));
const hotSongs = computed(() => {
  const next = props.songs.slice(6, 12);
  return next.length ? next : props.songs.slice(0, 6);
});

const currentPlaylist = computed(() =>
  props.playlists.find((item) => Number(item.id) === Number(props.selectedPlaylistId)) || null
);

function isFavorite(songId) {
  return props.favoriteIds.includes(songId);
}
</script>

<template>
  <div v-if="isLoading" class="empty-card">
    <strong>歌曲加载中</strong>
    <p>请稍候，正在从后端读取数据。</p>
  </div>

  <div v-else-if="errorMessage" class="empty-card">
    <strong>歌曲加载失败</strong>
    <p>{{ errorMessage }}</p>
    <button class="play-button" @click="emit('reload')">重新加载</button>
  </div>

  <div v-else-if="currentView === 'home' && !searchKeyword" class="dashboard-stack">
    <section class="stack-section">
      <div class="section-headline">
        <h3>为你推荐</h3>
        <span>从本地曲库精选出的氛围歌单</span>
      </div>
      <div class="media-grid">
        <SongCard
          v-for="song in recommendedSongs"
          :key="song.id"
          :song="song"
          :favorite="isFavorite(song.id)"
          @play="emit('play', song, recommendedSongs)"
          @toggle-favorite="emit('toggle-favorite', song)"
          @open-playlist="emit('open-playlist', song)"
        />
      </div>
    </section>

    <section class="stack-section">
      <div class="section-headline">
        <h3>热门歌单</h3>
        <span>更适合展示和答辩的音乐软件布局</span>
      </div>
      <div class="media-grid">
        <SongCard
          v-for="song in hotSongs"
          :key="`hot-${song.id}`"
          :song="song"
          :favorite="isFavorite(song.id)"
          @play="emit('play', song, hotSongs)"
          @toggle-favorite="emit('toggle-favorite', song)"
          @open-playlist="emit('open-playlist', song)"
        />
      </div>
    </section>
  </div>

  <div v-else-if="currentView === 'home' && searchKeyword" class="track-panel">
    <div v-if="songs.length" class="track-list">
      <article v-for="song in songs" :key="song.id" class="track-row" :class="{ active: currentSongId === song.id }" @click="emit('play', song, songs)">
        <div class="track-art"><img :src="song.cover" :alt="song.name"></div>
        <div class="track-main">
          <strong>{{ song.name }}</strong>
          <span>{{ song.singer || '未知歌手' }}</span>
        </div>
        <div class="track-album">{{ song.album || '未命名专辑' }}</div>
        <div class="track-duration">{{ song.durationLabel }}</div>
        <div class="track-actions" @click.stop>
          <button class="small-pill" :class="{ active: isFavorite(song.id) }" @click="emit('toggle-favorite', song)">
            {{ isFavorite(song.id) ? "♥" : "♡" }}
          </button>
          <button class="small-pill" @click="emit('open-playlist', song)">＋ 歌单</button>
        </div>
      </article>
    </div>
    <div v-else class="empty-card">
      <strong>没有搜索到结果</strong>
      <p>换一个歌手、歌曲或专辑名试试。</p>
    </div>
  </div>

  <div v-else-if="currentView === 'favorites'" class="track-panel">
    <div v-if="!currentUser" class="empty-card">
      <strong>请先登录</strong>
      <p>登录后即可查看并维护你的收藏歌曲。</p>
      <button class="play-button" @click="emit('open-auth')">去登录</button>
    </div>
    <div v-else-if="favoriteSongs.length" class="track-list">
      <article v-for="song in favoriteSongs" :key="song.id" class="track-row" :class="{ active: currentSongId === song.id }" @click="emit('play', song, favoriteSongs)">
        <div class="track-art"><img :src="song.cover" :alt="song.name"></div>
        <div class="track-main">
          <strong>{{ song.name }}</strong>
          <span>{{ song.singer || '未知歌手' }}</span>
        </div>
        <div class="track-album">{{ song.album || '未命名专辑' }}</div>
        <div class="track-duration">{{ song.durationLabel }}</div>
        <div class="track-actions" @click.stop>
          <button class="small-pill active" @click="emit('toggle-favorite', song)">♥</button>
          <button class="small-pill" @click="emit('open-playlist', song)">＋ 歌单</button>
        </div>
      </article>
    </div>
    <div v-else class="empty-card">
      <strong>还没有收藏歌曲</strong>
      <p>点击歌曲卡片上的“喜欢”按钮即可加入这里。</p>
    </div>
  </div>

  <div v-else-if="currentView === 'recent'" class="track-panel">
    <div v-if="!currentUser" class="empty-card">
      <strong>请先登录</strong>
      <p>登录后即可查看最近播放记录。</p>
      <button class="play-button" @click="emit('open-auth')">去登录</button>
    </div>
    <div v-else-if="recentSongs.length" class="track-list">
      <article v-for="song in recentSongs" :key="song.id" class="track-row" :class="{ active: currentSongId === song.id }" @click="emit('play', song, recentSongs)">
        <div class="track-art"><img :src="song.cover" :alt="song.name"></div>
        <div class="track-main">
          <strong>{{ song.name }}</strong>
          <span>{{ song.singer || '未知歌手' }}</span>
        </div>
        <div class="track-album">{{ song.album || '未命名专辑' }}</div>
        <div class="track-duration">{{ song.durationLabel }}</div>
        <div class="track-actions" @click.stop>
          <button class="small-pill" :class="{ active: isFavorite(song.id) }" @click="emit('toggle-favorite', song)">
            {{ isFavorite(song.id) ? "♥" : "♡" }}
          </button>
          <button class="small-pill" @click="emit('open-playlist', song)">＋ 歌单</button>
        </div>
      </article>
    </div>
    <div v-else class="empty-card">
      <strong>最近播放为空</strong>
      <p>先播放几首歌曲，这里就会出现记录。</p>
    </div>
  </div>

  <div v-else-if="currentView === 'playlists'" class="playlist-view">
    <div v-if="!currentUser" class="empty-card">
      <strong>登录后查看我的歌单</strong>
      <p>创建、管理并维护你的个人歌单。</p>
      <button class="play-button" @click="emit('open-auth')">立即登录</button>
    </div>
    <template v-else>
      <div class="playlist-header">
        <div class="playlist-meta">
          <strong>我的歌单</strong>
          <span>创建歌单、查看内容并在当前页面管理歌曲。</span>
        </div>
      </div>

      <div v-if="playlists.length" class="playlist-grid">
        <article
          v-for="playlist in playlists"
          :key="playlist.id"
          class="playlist-card"
          @click="emit('select-playlist', playlist.id)"
        >
          <div class="playlist-cover">
            <img :src="playlist.cover || '/cover/default.jpg'" :alt="playlist.name">
          </div>
          <div>
            <strong>{{ playlist.name || "未命名歌单" }}</strong>
            <p>{{ playlist.description || "暂无描述" }}</p>
          </div>
        </article>
      </div>
      <div v-else class="empty-card">
        <strong>还没有歌单</strong>
        <p>先创建一个歌单，再把喜欢的歌曲收进去。</p>
      </div>

      <section v-if="currentPlaylist" class="playlist-view">
        <div class="playlist-header">
          <div class="playlist-meta">
            <strong>{{ currentPlaylist.name }}</strong>
            <span>{{ currentPlaylist.description || "这个歌单暂时没有描述。" }}</span>
          </div>
          <span class="result-pill">{{ playlistSongs.length }} 首歌曲</span>
        </div>
        <div v-if="playlistSongs.length" class="track-list">
          <article v-for="song in playlistSongs" :key="song.id" class="track-row" :class="{ active: currentSongId === song.id }" @click="emit('play', song, playlistSongs)">
            <div class="track-art"><img :src="song.cover" :alt="song.name"></div>
            <div class="track-main">
              <strong>{{ song.name }}</strong>
              <span>{{ song.singer || '未知歌手' }}</span>
            </div>
            <div class="track-album">{{ song.album || '未命名专辑' }}</div>
            <div class="track-duration">{{ song.durationLabel }}</div>
            <div class="track-actions" @click.stop>
              <button class="small-pill" @click="emit('remove-playlist-song', song)">移出</button>
            </div>
          </article>
        </div>
        <div v-else class="empty-card">
          <strong>歌单还是空的</strong>
          <p>从首页、收藏或最近播放中把歌曲加入这里。</p>
        </div>
      </section>
    </template>
  </div>

  <div v-else class="empty-card">
    <strong>{{ placeholderMap[currentView]?.[0] || "内容区域" }}</strong>
    <p>{{ placeholderMap[currentView]?.[1] || "暂无内容" }}</p>
  </div>
</template>
