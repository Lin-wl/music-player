<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  keyword: {
    type: String,
    default: ""
  },
  currentUser: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(["update:keyword", "search", "profile-click"]);

const localKeyword = ref(props.keyword);

watch(
  () => props.keyword,
  (value) => {
    localKeyword.value = value;
  }
);

function submitSearch() {
  emit("update:keyword", localKeyword.value);
  emit("search", localKeyword.value);
}

function goBack() {
  window.history.back();
}

function goForward() {
  window.history.forward();
}
</script>

<template>
  <header class="topbar glass-panel">
    <div class="topbar-left">
      <button class="icon-button" aria-label="返回" @click="goBack">←</button>
      <button class="icon-button" aria-label="前进" @click="goForward">→</button>
      <div class="search-wrap">
        <span class="search-icon">⌕</span>
        <input
          v-model="localKeyword"
          type="text"
          placeholder="搜索歌手、歌曲、专辑或歌单"
          @keydown.enter.prevent="submitSearch"
        >
        <button class="search-action" @click="submitSearch">搜索</button>
      </div>
    </div>

    <div class="topbar-right">
      <button class="ghost-button">升级会员</button>
      <button class="icon-button notify-button" aria-label="通知">
        <span>🔔</span>
        <span class="notify-badge">2</span>
      </button>
      <button class="profile-chip" @click="emit('profile-click')">
        <div class="profile-avatar">
          {{ (currentUser?.nickname || currentUser?.username || "登").charAt(0).toUpperCase() }}
        </div>
        <div class="profile-copy">
          <span class="profile-label">欢迎回来</span>
          <strong>{{ currentUser?.nickname || currentUser?.username || "登录 / 注册" }}</strong>
        </div>
        <span class="profile-arrow">⌄</span>
      </button>
    </div>
  </header>
</template>
