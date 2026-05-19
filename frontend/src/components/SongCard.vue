<script setup>
defineProps({
  song: {
    type: Object,
    required: true
  },
  favorite: {
    type: Boolean,
    default: false
  }
});

defineEmits(["play", "toggle-favorite", "open-playlist"]);
</script>

<template>
  <article class="media-card">
    <div class="media-cover">
      <img :src="song.cover" :alt="song.name">
      <button class="media-overlay" title="播放" @click="$emit('play', song)">▶</button>
    </div>
    <div class="media-meta">
      <strong>{{ song.name || "未知歌曲" }}</strong>
      <p>{{ `${song.singer || "未知歌手"} · ${song.album || "未命名专辑"}` }}</p>
      <p>{{ song.durationLabel }}</p>
    </div>
    <div class="track-actions">
      <button class="small-pill" :class="{ active: favorite }" @click="$emit('toggle-favorite', song)">
        {{ favorite ? "♥ 已喜欢" : "♡ 喜欢" }}
      </button>
      <button class="small-pill" @click="$emit('open-playlist', song)">＋ 歌单</button>
    </div>
  </article>
</template>
