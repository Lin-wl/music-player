<script setup>
defineProps({
  currentSong: { type: Object, default: null },
  isPlaying: { type: Boolean, default: false },
  isFavorite: { type: Boolean, default: false },
  shuffle: { type: Boolean, default: false },
  repeat: { type: Boolean, default: false },
  currentTimeLabel: { type: String, default: "00:00" },
  totalTimeLabel: { type: String, default: "00:00" },
  progressPercent: { type: Number, default: 0 },
  volume: { type: Number, default: 0.7 }
});

defineEmits([
  "prev",
  "next",
  "toggle-play",
  "toggle-favorite",
  "toggle-shuffle",
  "toggle-repeat",
  "seek",
  "update-volume",
  "scroll-queue",
  "fullscreen"
]);
</script>

<template>
  <footer class="player-bar glass-panel">
    <div class="player-left">
      <img :src="currentSong?.cover" alt="当前歌曲封面">
      <div class="player-track-info">
        <strong>{{ currentSong?.name || "请选择一首歌曲开始播放" }}</strong>
        <span>{{ currentSong ? `${currentSong.singer || "未知歌手"} · ${currentSong.album || "未命名专辑"}` : "播放器已就绪" }}</span>
      </div>
      <button class="player-like" :class="{ active: isFavorite }" @click="$emit('toggle-favorite')">♥</button>
    </div>

    <div class="player-center">
      <div class="player-controls">
        <button class="icon-button small" :class="{ active: shuffle }" @click="$emit('toggle-shuffle')">⇄</button>
        <button class="icon-button small" @click="$emit('prev')">⏮</button>
        <button class="player-primary" @click="$emit('toggle-play')">{{ isPlaying ? "❚❚" : "▶" }}</button>
        <button class="icon-button small" @click="$emit('next')">⏭</button>
        <button class="icon-button small" :class="{ active: repeat }" @click="$emit('toggle-repeat')">↻</button>
      </div>
      <div class="player-progress-wrap">
        <span>{{ currentTimeLabel }}</span>
        <input type="range" min="0" max="100" :value="progressPercent" @input="$emit('seek', Number($event.target.value))">
        <span>{{ totalTimeLabel }}</span>
      </div>
    </div>

    <div class="player-right">
      <button class="icon-button small" @click="$emit('scroll-queue')">☰</button>
      <span class="volume-icon">🔊</span>
      <input type="range" min="0" max="1" step="0.01" :value="volume" @input="$emit('update-volume', Number($event.target.value))">
      <button class="icon-button small" @click="$emit('fullscreen')">⤢</button>
    </div>
  </footer>
</template>
