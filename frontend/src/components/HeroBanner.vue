<script setup>
defineProps({
  songCount: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  }
});

defineEmits(["play", "refresh"]);
</script>

<template>
  <section class="hero-panel glass-panel">
    <div class="hero-copy">
      <span class="hero-tag">每日推荐</span>
      <h2>音乐的力量</h2>
      <h3>陪伴你的每一刻</h3>
      <p>{{ loading ? "正在同步歌曲列表，请稍候。" : `当前曲库共 ${songCount} 首歌，点击任意卡片即可开始播放。` }}</p>
      <div class="hero-actions">
        <button class="play-button" @click="$emit('play')">▶ 播放</button>
        <button class="ghost-button" @click="$emit('refresh')">刷新曲库</button>
      </div>
    </div>
    <div class="hero-art">
      <div class="hero-glow"></div>
      <div class="hero-disc"></div>
      <div class="hero-silhouette"></div>
    </div>
  </section>
</template>
