import axios from "axios";

const apiClient = axios.create({
  baseURL: "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json"
  }
});

apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || error.message || "请求失败";
    return Promise.reject(new Error(message));
  }
);

export const BACKEND_BASE_URL = "http://localhost:8080";

export function unwrapList(payload) {
  if (Array.isArray(payload)) {
    return payload;
  }
  if (payload && Array.isArray(payload.data)) {
    return payload.data;
  }
  return [];
}

export function unwrapObject(payload) {
  return payload?.data ?? payload;
}

export function toMediaUrl(url, fallback = "") {
  if (!url) {
    return fallback;
  }
  if (/^https?:\/\//i.test(url) || url.startsWith("data:")) {
    return url;
  }
  return `${BACKEND_BASE_URL}${url.startsWith("/") ? "" : "/"}${url}`;
}

export function getAllSongs() {
  return apiClient.get("/songs");
}

export function searchSongs(keyword) {
  return apiClient.get("/songs/search", {
    params: { keyword }
  });
}

export function loginUser(payload) {
  return apiClient.post("/users/login", payload);
}

export function registerUser(payload) {
  return apiClient.post("/users/register", payload);
}

export function getFavorites(userId) {
  return apiClient.get(`/favorites/user/${userId}`);
}

export function addFavorite(userId, songId) {
  return apiClient.post(`/favorites?userId=${userId}&songId=${songId}`);
}

export function removeFavorite(userId, songId) {
  return apiClient.delete(`/favorites?userId=${userId}&songId=${songId}`);
}

export function getRecentSongs(userId) {
  return apiClient.get(`/play-history/user/${userId}`);
}

export function recordPlayHistory(userId, songId) {
  return apiClient.post(`/play-history?userId=${userId}&songId=${songId}`);
}

export function getUserPlaylists(userId) {
  return apiClient.get(`/playlists/user/${userId}`);
}

export function createPlaylist(payload) {
  return apiClient.post("/playlists", payload);
}

export function getPlaylistSongs(playlistId) {
  return apiClient.get(`/playlists/${playlistId}/songs`);
}

export function addSongToPlaylist(playlistId, songId) {
  return apiClient.post(`/playlists/${playlistId}/songs/${songId}`);
}

export function removeSongFromPlaylist(playlistId, songId) {
  return apiClient.delete(`/playlists/${playlistId}/songs/${songId}`);
}
