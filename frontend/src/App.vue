<script setup>
import { computed, nextTick, onMounted, ref } from "vue";
import Sidebar from "./components/Sidebar.vue";
import TopBar from "./components/TopBar.vue";
import HeroBanner from "./components/HeroBanner.vue";
import SongList from "./components/SongList.vue";
import RightPanel from "./components/RightPanel.vue";
import PlayerBar from "./components/PlayerBar.vue";
import {
  addFavorite,
  addSongToPlaylist,
  BACKEND_BASE_URL,
  createPlaylist,
  getAllSongs,
  getFavorites,
  getPlaylistSongs,
  getRecentSongs,
  getUserPlaylists,
  loginUser,
  recordPlayHistory,
  registerUser,
  removeFavorite,
  removeSongFromPlaylist,
  searchSongs,
  toMediaUrl,
  unwrapList,
  unwrapObject
} from "./api/song";

const STORAGE_KEY = "music_player_current_user";
const DEFAULT_COVER = `data:image/svg+xml;utf8,${encodeURIComponent(`
  <svg xmlns="http://www.w3.org/2000/svg" width="400" height="400" viewBox="0 0 400 400">
    <defs>
      <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#0f2417"/>
        <stop offset="100%" stop-color="#0a100c"/>
      </linearGradient>
    </defs>
    <rect width="400" height="400" rx="48" fill="url(#g)"/>
    <circle cx="200" cy="200" r="118" fill="rgba(30,215,96,0.10)" stroke="#1ed760" stroke-width="10"/>
    <circle cx="200" cy="200" r="42" fill="#081108" stroke="#eef7f0" stroke-opacity="0.25" stroke-width="6"/>
    <text x="50%" y="88%" dominant-baseline="middle" text-anchor="middle" fill="#dce7df" font-size="36" font-family="Segoe UI, Arial">MELODY</text>
  </svg>
`)}`;

const audioRef = ref(null);
const songs = ref([]);
const filteredSongs = ref([]);
const favoriteSongs = ref([]);
const recentSongs = ref([]);
const playlists = ref([]);
const playlistSongs = ref([]);
const selectedPlaylistId = ref(null);
const currentQueue = ref([]);
const currentSong = ref(null);
const currentSongIndex = ref(-1);
const currentView = ref("home");
const searchKeyword = ref("");
const isLoading = ref(false);
const errorMessage = ref("");
const currentUser = ref(null);
const favoriteIds = ref([]);
const authMode = ref("login");
const authUsername = ref("");
const authPassword = ref("");
const authNickname = ref("");
const authMessage = ref("");
const showAuthModal = ref(false);
const playlistModalSong = ref(null);
const showPlaylistModal = ref(false);
const playlistMessage = ref("");
const newPlaylistName = ref("");
const newPlaylistDescription = ref("");
const feedbackMessage = ref("");
const feedbackType = ref("info");
const currentTime = ref(0);
const totalTime = ref(0);
const volume = ref(0.7);
const shuffle = ref(false);
const repeat = ref(false);

const currentTimeLabel = computed(() => formatTime(currentTime.value));
const totalTimeLabel = computed(() => formatTime(totalTime.value));
const progressPercent = computed(() => (totalTime.value > 0 ? (currentTime.value / totalTime.value) * 100 : 0));
const isPlaying = ref(false);
const isCurrentFavorite = computed(() => currentSong.value ? favoriteIds.value.includes(currentSong.value.id) : false);
const displayedSongs = computed(() => filteredSongs.value);
const queue = computed(() => currentQueue.value.length ? currentQueue.value : displayedSongs.value);
const shouldShowHero = computed(() => currentView.value === "home" && !searchKeyword.value && !errorMessage.value);
const sectionTitle = computed(() => {
  if (currentView.value === "home" && searchKeyword.value) return "搜索结果";
  if (currentView.value === "favorites") return "我喜欢的音乐";
  if (currentView.value === "recent") return "最近播放";
  if (currentView.value === "playlists") return "我的歌单";
  if (currentView.value === "podcast") return "播客";
  if (currentView.value === "video") return "视频";
  if (currentView.value === "follow") return "关注";
  if (currentView.value === "local") return "本地与下载";
  return "为你推荐";
});
const sectionDescription = computed(() => {
  if (currentView.value === "home" && searchKeyword.value) return `关键词：${searchKeyword.value}`;
  if (currentView.value === "favorites") return "已登录用户的收藏内容会显示在这里。";
  if (currentView.value === "recent") return "系统会按播放时间记录最近播放。";
  if (currentView.value === "playlists") return "查看、创建歌单并维护歌单中的歌曲。";
  if (currentView.value === "podcast") return "这个区域保留给后续播客内容扩展。";
  if (currentView.value === "video") return "这里可以在后续版本中接入 MV 或短视频内容。";
  if (currentView.value === "follow") return "这里可以扩展成好友关注或音乐社区动态。";
  if (currentView.value === "local") return "本地资源已通过后端静态映射提供，这里先保留入口视图。";
  return "根据当前歌曲库生成的推荐内容。";
});
const resultCountLabel = computed(() => {
  if (currentView.value === "favorites") return `${favoriteSongs.value.length} 项内容`;
  if (currentView.value === "recent") return `${recentSongs.value.length} 项内容`;
  if (currentView.value === "playlists") return `${playlists.value.length} 项内容`;
  return `${displayedSongs.value.length} 首歌曲`;
});

function normalizeSong(song) {
  const duration = song.duration;
  return {
    ...song,
    cover: toMediaUrl(song.cover, DEFAULT_COVER),
    url: toMediaUrl(song.url, `${BACKEND_BASE_URL}/music`),
    durationLabel: typeof duration === "number" ? formatTime(duration) : (duration || "00:00")
  };
}

function formatTime(value) {
  const total = Number(value || 0);
  const minutes = Math.floor(total / 60);
  const seconds = Math.floor(total % 60);
  return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
}

function setFeedback(message = "", type = "info") {
  feedbackMessage.value = message;
  feedbackType.value = type;
}

function ensureLoggedIn() {
  if (currentUser.value) {
    return true;
  }
  setFeedback("请先登录后再执行该操作。", "warning");
  openAuthModal("login");
  return false;
}

function restoreUser() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return;
  }
  try {
    currentUser.value = JSON.parse(raw);
  } catch (error) {
    localStorage.removeItem(STORAGE_KEY);
  }
}

function saveUser(user) {
  currentUser.value = user;
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

function clearUser() {
  currentUser.value = null;
  localStorage.removeItem(STORAGE_KEY);
  favoriteSongs.value = [];
  recentSongs.value = [];
  playlists.value = [];
  playlistSongs.value = [];
  favoriteIds.value = [];
  selectedPlaylistId.value = null;
}

async function loadSongs() {
  isLoading.value = true;
  errorMessage.value = "";
  try {
    const payload = await getAllSongs();
    songs.value = unwrapList(payload).map(normalizeSong);
    if (!searchKeyword.value) {
      filteredSongs.value = songs.value.slice();
    }
  } catch (error) {
    errorMessage.value = error.message || "歌曲加载失败，请检查后端服务是否启动。";
  } finally {
    isLoading.value = false;
  }
}

async function handleSearch(keyword = searchKeyword.value) {
  searchKeyword.value = keyword.trim();
  if (!searchKeyword.value) {
    filteredSongs.value = songs.value.slice();
    currentView.value = "home";
    return;
  }
  isLoading.value = true;
  errorMessage.value = "";
  currentView.value = "home";
  try {
    const payload = await searchSongs(searchKeyword.value);
    filteredSongs.value = unwrapList(payload).map(normalizeSong);
  } catch (error) {
    filteredSongs.value = [];
    errorMessage.value = error.message || "搜索失败，请稍后重试。";
  } finally {
    isLoading.value = false;
  }
}

async function loadFavorites() {
  if (!currentUser.value) {
    favoriteSongs.value = [];
    favoriteIds.value = [];
    return;
  }
  try {
    const payload = await getFavorites(currentUser.value.id);
    favoriteSongs.value = unwrapList(payload).map(normalizeSong);
    favoriteIds.value = favoriteSongs.value.map((song) => song.id);
  } catch (error) {
    favoriteSongs.value = [];
    favoriteIds.value = [];
  }
}

async function loadRecentSongs() {
  if (!currentUser.value) {
    recentSongs.value = [];
    return;
  }
  try {
    const payload = await getRecentSongs(currentUser.value.id);
    recentSongs.value = unwrapList(payload).map(normalizeSong);
  } catch (error) {
    recentSongs.value = [];
  }
}

async function loadPlaylists() {
  if (!currentUser.value) {
    playlists.value = [];
    selectedPlaylistId.value = null;
    playlistSongs.value = [];
    return;
  }
  try {
    const payload = await getUserPlaylists(currentUser.value.id);
    playlists.value = unwrapList(payload).map((item) => ({
      ...item,
      cover: toMediaUrl(item.cover, DEFAULT_COVER)
    }));
    if (!selectedPlaylistId.value && playlists.value.length) {
      selectedPlaylistId.value = playlists.value[0].id;
    }
  } catch (error) {
    playlists.value = [];
  }
}

async function loadSelectedPlaylistSongs() {
  if (!selectedPlaylistId.value) {
    playlistSongs.value = [];
    return;
  }
  try {
    const payload = await getPlaylistSongs(selectedPlaylistId.value);
    playlistSongs.value = unwrapList(payload).map(normalizeSong);
  } catch (error) {
    playlistSongs.value = [];
  }
}

async function refreshUserData() {
  await Promise.all([loadFavorites(), loadRecentSongs(), loadPlaylists()]);
  await loadSelectedPlaylistSongs();
}

function findSongIndex(song, songList) {
  return songList.findIndex((item) => Number(item.id) === Number(song.id));
}

async function playSong(song, songList) {
  const queueSource = (songList && songList.length ? songList : songs.value).slice();
  currentQueue.value = queueSource;
  currentSongIndex.value = findSongIndex(song, queueSource);
  currentSong.value = queueSource[currentSongIndex.value] || song;
  await nextTick();
  if (audioRef.value) {
    audioRef.value.src = currentSong.value.url;
    try {
      await audioRef.value.play();
      isPlaying.value = true;
    } catch (error) {
      setFeedback("歌曲播放失败，请检查本地音乐文件路径是否可访问。", "warning");
    }
  }
  if (currentUser.value && currentSong.value) {
    recordPlayHistory(currentUser.value.id, currentSong.value.id).catch(() => {});
    loadRecentSongs();
  }
}

function playNext() {
  const queueSource = queue.value;
  if (!queueSource.length) {
    return;
  }
  let nextIndex = currentSongIndex.value + 1;
  if (shuffle.value && queueSource.length > 1) {
    do {
      nextIndex = Math.floor(Math.random() * queueSource.length);
    } while (nextIndex === currentSongIndex.value);
  } else if (nextIndex >= queueSource.length) {
    nextIndex = 0;
  }
  playSong(queueSource[nextIndex], queueSource);
}

function playPrev() {
  const queueSource = queue.value;
  if (!queueSource.length) {
    return;
  }
  let prevIndex = currentSongIndex.value - 1;
  if (prevIndex < 0) {
    prevIndex = queueSource.length - 1;
  }
  playSong(queueSource[prevIndex], queueSource);
}

function togglePlayback() {
  if (!currentSong.value) {
    if (songs.value.length) {
      playSong(songs.value[0], songs.value);
    }
    return;
  }
  if (!audioRef.value) {
    return;
  }
  if (audioRef.value.paused) {
    audioRef.value.play().then(() => {
      isPlaying.value = true;
    }).catch(() => {
      setFeedback("播放失败，请检查浏览器是否阻止了自动播放。", "warning");
    });
  } else {
    audioRef.value.pause();
    isPlaying.value = false;
  }
}

function handleTimeUpdate() {
  if (!audioRef.value) {
    return;
  }
  currentTime.value = audioRef.value.currentTime || 0;
  totalTime.value = audioRef.value.duration || 0;
}

function handleLoadedMetadata() {
  if (!audioRef.value) {
    return;
  }
  totalTime.value = audioRef.value.duration || 0;
}

function handleAudioPlay() {
  isPlaying.value = true;
}

function handleAudioPause() {
  isPlaying.value = false;
}

function handleSeek(percent) {
  if (!audioRef.value || !audioRef.value.duration) {
    return;
  }
  audioRef.value.currentTime = (percent / 100) * audioRef.value.duration;
}

function handleVolume(value) {
  volume.value = value;
  if (audioRef.value) {
    audioRef.value.volume = value;
  }
}

function handleEnded() {
  isPlaying.value = false;
  if (repeat.value && audioRef.value) {
    audioRef.value.currentTime = 0;
    audioRef.value.play().then(() => {
      isPlaying.value = true;
    }).catch(() => {});
    return;
  }
  playNext();
}

async function toggleFavoriteSong(song = currentSong.value) {
  if (!song || !ensureLoggedIn()) {
    return;
  }
  const exists = favoriteIds.value.includes(song.id);
  try {
    if (exists) {
      await removeFavorite(currentUser.value.id, song.id);
      setFeedback("已取消收藏。");
    } else {
      await addFavorite(currentUser.value.id, song.id);
      setFeedback("收藏成功。");
    }
    await loadFavorites();
  } catch (error) {
    setFeedback(error.message || "收藏操作失败。", "warning");
  }
}

function openAuthModal(mode = "login") {
  authMode.value = mode;
  authMessage.value = "";
  authUsername.value = "";
  authPassword.value = "";
  authNickname.value = "";
  showAuthModal.value = true;
}

function closeAuthModal() {
  showAuthModal.value = false;
  authMessage.value = "";
}

async function submitAuth() {
  if (!authUsername.value.trim() || !authPassword.value.trim() || (authMode.value === "register" && !authNickname.value.trim())) {
    authMessage.value = "请完整填写表单。";
    return;
  }
  try {
    const payload = authMode.value === "login"
      ? await loginUser({ username: authUsername.value.trim(), password: authPassword.value.trim() })
      : await registerUser({ username: authUsername.value.trim(), password: authPassword.value.trim(), nickname: authNickname.value.trim() });
    saveUser(unwrapObject(payload));
    closeAuthModal();
    await refreshUserData();
    setFeedback(authMode.value === "login" ? "登录成功。" : "注册成功，已自动登录。");
  } catch (error) {
    authMessage.value = error.message || "操作失败，请稍后重试。";
  }
}

function handleProfileClick() {
  if (!currentUser.value) {
    openAuthModal("login");
    return;
  }
  if (window.confirm("是否退出当前账号？")) {
    clearUser();
    setFeedback("已退出登录。");
  }
}

async function openPlaylistModal(song = null) {
  if (!ensureLoggedIn()) {
    return;
  }
  playlistModalSong.value = song;
  playlistMessage.value = "";
  newPlaylistName.value = "";
  newPlaylistDescription.value = "";
  await loadPlaylists();
  showPlaylistModal.value = true;
}

function closePlaylistModal() {
  showPlaylistModal.value = false;
  playlistModalSong.value = null;
  playlistMessage.value = "";
}

async function pickPlaylist(playlistId) {
  if (!playlistModalSong.value) {
    return;
  }
  try {
    await addSongToPlaylist(playlistId, playlistModalSong.value.id);
    playlistMessage.value = "已添加到歌单。";
    await refreshUserData();
  } catch (error) {
    playlistMessage.value = error.message || "添加到歌单失败。";
  }
}

async function submitCreatePlaylist() {
  if (!ensureLoggedIn()) {
    return;
  }
  if (!newPlaylistName.value.trim()) {
    playlistMessage.value = "请输入歌单名称。";
    return;
  }
  try {
    const payload = await createPlaylist({
      userId: currentUser.value.id,
      name: newPlaylistName.value.trim(),
      description: newPlaylistDescription.value.trim()
    });
    const created = unwrapObject(payload);
    await loadPlaylists();
    selectedPlaylistId.value = created?.id || selectedPlaylistId.value;
    if (playlistModalSong.value && created?.id) {
      await addSongToPlaylist(created.id, playlistModalSong.value.id);
    }
    await loadSelectedPlaylistSongs();
    playlistMessage.value = "歌单创建成功。";
  } catch (error) {
    playlistMessage.value = error.message || "创建歌单失败。";
  }
}

async function handleRemoveSongFromPlaylist(song) {
  if (!selectedPlaylistId.value) {
    return;
  }
  try {
    await removeSongFromPlaylist(selectedPlaylistId.value, song.id);
    setFeedback("已从歌单移除。");
    await loadSelectedPlaylistSongs();
  } catch (error) {
    setFeedback(error.message || "移出歌单失败。", "warning");
  }
}

async function changeView(view) {
  currentView.value = view;
  if (view === "playlists") {
    await loadSelectedPlaylistSongs();
  }
}

async function selectPlaylist(playlistId) {
  selectedPlaylistId.value = playlistId;
  await loadSelectedPlaylistSongs();
}

function toggleShuffle() {
  shuffle.value = !shuffle.value;
}

function toggleRepeat() {
  repeat.value = !repeat.value;
}

function scrollToQueue() {
  document.querySelector(".queue-card")?.scrollIntoView({ behavior: "smooth", block: "center" });
}

function toggleFullscreen() {
  if (document.fullscreenElement) {
    document.exitFullscreen?.();
  } else {
    document.documentElement.requestFullscreen?.();
  }
}

onMounted(async () => {
  restoreUser();
  await loadSongs();
  if (currentUser.value) {
    await refreshUserData();
  }
  if (audioRef.value) {
    audioRef.value.volume = volume.value;
  }
});
</script>

<template>
  <div class="app-shell">
    <Sidebar :current-view="currentView" @change-view="changeView" />

    <div class="main-shell">
      <TopBar
        v-model:keyword="searchKeyword"
        :current-user="currentUser"
        @search="handleSearch"
        @profile-click="handleProfileClick"
      />

      <div class="workspace">
        <main class="content-area">
          <HeroBanner v-if="shouldShowHero" :song-count="songs.length" :loading="isLoading" @play="songs.length && playSong(songs[0], songs)" @refresh="loadSongs" />

          <section class="section-intro">
            <div>
              <h2>{{ sectionTitle }}</h2>
              <p>{{ sectionDescription }}</p>
            </div>
            <span class="result-pill">{{ resultCountLabel }}</span>
          </section>

          <section v-if="feedbackMessage" class="feedback-banner" :class="feedbackType">
            {{ feedbackMessage }}
          </section>

          <SongList
            :current-view="currentView"
            :search-keyword="searchKeyword"
            :songs="displayedSongs"
            :favorite-songs="favoriteSongs"
            :recent-songs="recentSongs"
            :playlists="playlists"
            :playlist-songs="playlistSongs"
            :selected-playlist-id="selectedPlaylistId"
            :current-song-id="currentSong?.id"
            :favorite-ids="favoriteIds"
            :current-user="currentUser"
            :is-loading="isLoading"
            :error-message="errorMessage"
            @play="playSong"
            @toggle-favorite="toggleFavoriteSong"
            @open-playlist="openPlaylistModal"
            @select-playlist="selectPlaylist"
            @remove-playlist-song="handleRemoveSongFromPlaylist"
            @open-auth="openAuthModal('login')"
            @reload="loadSongs"
          />
        </main>

        <RightPanel
          :current-song="currentSong || { cover: DEFAULT_COVER }"
          :is-playing="isPlaying"
          :queue="queue"
          :current-song-id="currentSong?.id"
          :current-time-label="currentTimeLabel"
          :total-time-label="totalTimeLabel"
          :progress-percent="progressPercent"
          :is-favorite="isCurrentFavorite"
          @prev="playPrev"
          @next="playNext"
          @toggle-play="togglePlayback"
          @toggle-favorite="toggleFavoriteSong()"
          @play-song="playSong"
        />
      </div>
    </div>

    <PlayerBar
      :current-song="currentSong || { cover: DEFAULT_COVER }"
      :is-playing="isPlaying"
      :is-favorite="isCurrentFavorite"
      :shuffle="shuffle"
      :repeat="repeat"
      :current-time-label="currentTimeLabel"
      :total-time-label="totalTimeLabel"
      :progress-percent="progressPercent"
      :volume="volume"
      @prev="playPrev"
      @next="playNext"
      @toggle-play="togglePlayback"
      @toggle-favorite="toggleFavoriteSong()"
      @toggle-shuffle="toggleShuffle"
      @toggle-repeat="toggleRepeat"
      @seek="handleSeek"
      @update-volume="handleVolume"
      @scroll-queue="scrollToQueue"
      @fullscreen="toggleFullscreen"
    />

    <audio
      ref="audioRef"
      preload="metadata"
      @timeupdate="handleTimeUpdate"
      @loadedmetadata="handleLoadedMetadata"
      @play="handleAudioPlay"
      @pause="handleAudioPause"
      @ended="handleEnded"
      @error="setFeedback('音频文件无法播放，请检查数据库中的 url 路径和本地静态资源映射。', 'warning')"
    ></audio>

    <div v-if="showAuthModal" class="modal-backdrop" @click.self="closeAuthModal">
      <div class="modal-card">
        <button class="modal-close" @click="closeAuthModal">×</button>
        <h3>{{ authMode === "login" ? "用户登录" : "用户注册" }}</h3>
        <p class="modal-subtitle">登录后可收藏歌曲、查看最近播放和管理歌单。</p>
        <form class="modal-form" @submit.prevent="submitAuth">
          <input v-model="authUsername" type="text" placeholder="用户名" required>
          <input v-model="authPassword" type="password" placeholder="密码" required>
          <input v-if="authMode === 'register'" v-model="authNickname" type="text" placeholder="昵称（注册时填写）" required>
          <button type="submit" class="modal-submit">{{ authMode === "login" ? "登录" : "注册" }}</button>
        </form>
        <p v-if="authMessage" class="modal-message">{{ authMessage }}</p>
        <button class="modal-switch" @click="authMode = authMode === 'login' ? 'register' : 'login'">
          {{ authMode === "login" ? "没有账号？去注册" : "已有账号？去登录" }}
        </button>
      </div>
    </div>

    <div v-if="showPlaylistModal" class="modal-backdrop" @click.self="closePlaylistModal">
      <div class="modal-card">
        <button class="modal-close" @click="closePlaylistModal">×</button>
        <h3>加入歌单</h3>
        <p class="modal-subtitle">
          {{ playlistModalSong ? `将《${playlistModalSong.name}》加入歌单` : "创建新歌单或选择现有歌单" }}
        </p>
        <div v-if="currentUser && playlists.length" class="playlist-picker-list">
          <button v-for="playlist in playlists" :key="playlist.id" class="playlist-picker-item" @click="pickPlaylist(playlist.id)">
            <div class="queue-cover">
              <img :src="playlist.cover || DEFAULT_COVER" :alt="playlist.name">
            </div>
            <div class="queue-song">
              <strong>{{ playlist.name }}</strong>
              <span>{{ playlist.description || "暂无描述" }}</span>
            </div>
            <span class="queue-indicator">＋</span>
          </button>
        </div>
        <div v-else class="playlist-picker-empty">还没有歌单，先在下面创建一个。</div>

        <div class="modal-divider"></div>

        <form class="modal-form compact" @submit.prevent="submitCreatePlaylist">
          <input v-model="newPlaylistName" type="text" placeholder="新歌单名称">
          <input v-model="newPlaylistDescription" type="text" placeholder="歌单描述（可选）">
          <button type="submit" class="modal-submit">创建并添加</button>
        </form>
        <p v-if="playlistMessage" class="modal-message">{{ playlistMessage }}</p>
      </div>
    </div>
  </div>
</template>
