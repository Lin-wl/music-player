# Music Player

当前仓库已经迁移为前后端分离结构：

```text
music/
├── backend/                # 新的 Spring Boot 后端
├── frontend/               # 新的 Vue3 + Vite 前端
├── music-player-files/     # 本地音乐与封面资源
├── sql/                    # 原有 SQL
├── src/                    # 旧单体项目源码，保留作为迁移参考
├── pom.xml                 # 旧单体项目 pom，保留作为迁移参考
└── README.md
```

## 迁移说明

- `backend/` 是新的后端启动目录。
- `frontend/` 是新的前端启动目录。
- 原来的 `src/main/resources/static/` 前端文件没有删除，作为迁移参考保留。
- 以后推荐只使用：
  - 后端：`backend/`
  - 前端：`frontend/`

## 后端启动

在 `backend/` 目录执行：

```powershell
$PSVersionTable.PSVersion
./mvnw.cmd spring-boot:run
```

后端默认地址：

- [http://localhost:8080](http://localhost:8080)

后端接口示例：

- [http://localhost:8080/songs](http://localhost:8080/songs)
- [http://localhost:8080/songs/search?keyword=周杰伦](http://localhost:8080/songs/search?keyword=%E5%91%A8%E6%9D%B0%E4%BC%A6)

## 前端启动

先安装依赖：

```powershell
$PSVersionTable.PSVersion
npm install
```

再启动开发服务器：

```powershell
$PSVersionTable.PSVersion
npm run dev
```

前端访问地址：

- [http://localhost:5173](http://localhost:5173)
- [http://127.0.0.1:5173](http://127.0.0.1:5173)

## 跨域与代理

### 后端跨域

`backend/src/main/java/com/example/music/config/WebMvcConfig.java` 已添加跨域配置，允许：

- `http://localhost:5173`
- `http://127.0.0.1:5173`

### Vite 代理

`frontend/vite.config.js` 已配置：

- 前端请求 `/api/**`
- Vite 代理到 `http://localhost:8080`
- 同时去掉 `/api` 前缀再转发给后端

例如：

- 前端请求 `/api/songs`
- 实际转发到后端 `/songs`

这样做的好处是：

- 开发环境不容易碰到跨域问题
- 前端代码里不需要把接口地址写死成完整域名

## 媒体资源说明

后端仍然保留本地资源映射：

- [http://localhost:8080/music/song1.mp3](http://localhost:8080/music/song1.mp3)
- [http://localhost:8080/cover/default.jpg](http://localhost:8080/cover/default.jpg)

本地文件目录：

- `music-player-files/music/`
- `music-player-files/cover/`

数据库中建议保持：

- `song.url = /music/xxx.mp3`
- `song.cover = /cover/xxx.jpg`

## 如果前端请求失败

优先检查：

1. 后端是否已经在 `8080` 端口成功启动。
2. 直接访问 [http://localhost:8080/songs](http://localhost:8080/songs) 是否能返回 JSON。
3. 前端是否是通过 `frontend/` 目录执行的 `npm run dev`。
4. 浏览器控制台是否报了网络错误。
5. `vite.config.js` 的代理是否生效。

## 如果音乐不能播放

优先检查：

1. 数据库 `song.url` 是否为 `/music/xxx.mp3`。
2. 对应 mp3 文件是否真的存在于 `music-player-files/music/`。
3. 浏览器能否直接访问对应地址，例如：
   - [http://localhost:8080/music/song1.mp3](http://localhost:8080/music/song1.mp3)
4. 后端 `WebMvcConfig` 是否正常加载。

## 如果封面不显示

优先检查：

1. 数据库 `song.cover` 是否为 `/cover/xxx.jpg`。
2. 对应图片是否存在于 `music-player-files/cover/`。
3. 浏览器能否直接访问对应地址，例如：
   - [http://localhost:8080/cover/default.jpg](http://localhost:8080/cover/default.jpg)

## 已完成检查

- 新前端 `npm install` 已完成
- 新前端 `npm run build` 已通过
- 新后端 `./mvnw.cmd -DskipTests compile` 已通过

这说明当前迁移后的基础结构已经可编译，后续你只需要分别启动 `backend/` 和 `frontend/` 即可联调。
