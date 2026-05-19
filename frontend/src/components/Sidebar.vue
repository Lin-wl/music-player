<script setup>
const props = defineProps({
  currentView: {
    type: String,
    required: true
  }
});

const emit = defineEmits(["change-view"]);

const mainItems = [
  { key: "home", icon: "♫", label: "发现音乐" },
  { key: "podcast", icon: "◉", label: "播客" },
  { key: "video", icon: "▣", label: "视频" },
  { key: "follow", icon: "◎", label: "关注" }
];

const libraryItems = [
  { key: "favorites", icon: "♥", label: "我喜欢的音乐" },
  { key: "local", icon: "⇩", label: "本地与下载" },
  { key: "recent", icon: "◔", label: "最近播放" },
  { key: "playlists", icon: "≡", label: "我的歌单" }
];

const miniPlaylists = [
  { name: "轻松时刻", count: "30 首", color: "green" },
  { name: "睡前音乐", count: "45 首", color: "blue" },
  { name: "学习专注", count: "66 首", color: "orange" }
];
</script>

<template>
  <aside class="sidebar glass-panel">
    <div class="brand-block">
      <div class="brand-mark">
        <span class="brand-ring"></span>
        <span class="brand-dot"></span>
      </div>
      <div>
        <h1 class="brand-title">Melody</h1>
        <p class="brand-subtitle">Immersive Music Space</p>
      </div>
    </div>

    <div class="nav-group">
      <button
        v-for="item in mainItems"
        :key="item.key"
        class="nav-item"
        :class="{ active: currentView === item.key }"
        @click="emit('change-view', item.key)"
      >
        <span class="nav-icon">{{ item.icon }}</span>
        <span>{{ item.label }}</span>
      </button>
    </div>

    <div class="nav-divider"></div>

    <div class="nav-group">
      <p class="nav-label">我的音乐</p>
      <button
        v-for="item in libraryItems"
        :key="item.key"
        class="nav-item"
        :class="{ active: currentView === item.key }"
        @click="emit('change-view', item.key)"
      >
        <span class="nav-icon">{{ item.icon }}</span>
        <span>{{ item.label }}</span>
      </button>
    </div>

    <div class="sidebar-bottom">
      <div v-for="item in miniPlaylists" :key="item.name" class="mini-playlist">
        <div class="mini-cover" :class="item.color"></div>
        <div>
          <p class="mini-title">{{ item.name }}</p>
          <p class="mini-meta">{{ item.count }}</p>
        </div>
      </div>
    </div>
  </aside>
</template>
