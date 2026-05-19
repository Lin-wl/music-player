<script setup>
defineProps({
  currentSong: { type: Object, default: null },
  isPlaying: { type: Boolean, default: false },
  queue: { type: Array, default: () => [] },
  currentSongId: { type: [Number, String], default: null },
  currentTimeLabel: { type: String, default: "00:00" },
  totalTimeLabel: { type: String, default: "00:00" },
  progressPercent: { type: Number, default: 0 },
  isFavorite: { type: Boolean, default: false }
});

defineEmits(["prev", "next", "toggle-play", "toggle-favorite", "play-song"]);
</script>

<template>
  <aside class="right-rail">
    <section class="rail-card glass-panel">
      <div class="rail-head">
        <h3>正在播放</h3>
        <button class="rail-link" :class="{ active: isFavorite }" title="喜欢" @click="$emit('toggle-favorite')">♥</button>
      </div>
      <div class="rail-cover-wrap">
        <img :src="currentSong?.cover" alt="当前歌曲封面">
      </div>
      <div class="rail-song-meta">
        <strong>{{ currentSong?.name || "请选择一首歌曲" }}</strong>
        <span>{{ currentSong ? `${currentSong.singer || "未知歌手"} · ${currentSong.album || "未命名专辑"}` : "播放器已就绪" }}</span>
      </div>
      <div class="rail-progress-meta">
        <span>{{ currentTimeLabel }}</span>
        <span>{{ totalTimeLabel }}</span>
      </div>
      <div class="rail-progress">
        <div class="rail-progress-bar" :style="{ width: `${progressPercent}%` }"></div>
      </div>
      <div class="rail-controls">
        <button class="icon-button small" @click="$emit('prev')">⏮</button>
        <button class="rail-main-play" @click="$emit('toggle-play')">{{ isPlaying ? "❚❚" : "▶" }}</button>
        <button class="icon-button small" @click="$emit('next')">⏭</button>
      </div>
    </section>

    <section class="rail-card glass-panel queue-card">
      <div class="rail-head">
        <h3>播放列表</h3>
        <span class="queue-count">{{ queue.length }}</span>
      </div>
      <div v-if="queue.length" class="queue-list">
        <article
          v-for="song in queue"
          :key="song.id"
          class="queue-item"
          :class="{ active: currentSongId === song.id }"
          @click="$emit('play-song', song, queue)"
        >
          <div class="queue-cover">
            <img :src="song.cover" :alt="song.name">
          </div>
          <div class="queue-song">
            <strong>{{ song.name }}</strong>
            <span>{{ song.singer || "未知歌手" }}</span>
          </div>
          <span class="queue-indicator">{{ currentSongId === song.id ? "▮▮" : song.durationLabel }}</span>
        </article>
      </div>
      <div v-else class="playlist-picker-empty">当前播放队列为空</div>
    </section>

    <section class="rail-card glass-panel friend-card">
      <div class="rail-head">
        <h3>好友动态</h3>
      </div>
      <div class="friend-list">
        <div class="friend-item">
          <div class="friend-avatar">K</div>
          <div>
            <strong>Katrina</strong>
            <p>正在听《旅途的意义》</p>
          </div>
        </div>
        <div class="friend-item">
          <div class="friend-avatar">E</div>
          <div>
            <strong>Ethan</strong>
            <p>正在听《Shape of You》</p>
          </div>
        </div>
        <div class="friend-item">
          <div class="friend-avatar">V</div>
          <div>
            <strong>Vivian</strong>
            <p>正在听《突然好想你》</p>
          </div>
        </div>
      </div>
    </section>
  </aside>
</template>
