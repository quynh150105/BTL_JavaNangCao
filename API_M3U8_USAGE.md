# 🎬 API USAGE GUIDE - Lấy M3U8 Link & Phát Video

## 📺 TÓML TẮC

**Link M3u8 được trả về từ endpoint:**
```
GET /movies/detail/{slug}
GET /movies/{id}
GET /movies/favorites
```

**Vị trí M3u8 trong response:**
```
data.episodes[index].serverData[index].linkM3u8
```

---

## 🔗 ENDPOINT DETAILS

### 1️⃣ GET /movies/detail/{slug}
**Mục đích**: Lấy chi tiết phim + danh sách tập + link phát

```bash
curl "http://localhost:8080/movies/detail/quanh-ta-la-cac-sao"
```

**Response** (Simplified):
```json
{
  "status": 200,
  "message": "Lấy phim thành công",
  "data": {
    "id": 1,
    "name": "Quanh Ta Là Các Sao",
    "originName": "Our Everlasting",
    "slug": "quanh-ta-la-cac-sao",
    "episodes": [
      {
        "id": 1001,
        "serverName": "VietSub",
        "isAi": false,
        "serverData": [
          {
            "name": "Tập 1",
            "slug": "tap-1",
            "filename": "tap-1-vietsub",
            "linkEmbed": "https://ophim.cc/api/v9/watch/1001/tap-1",
            "linkM3u8": "https://ophim.cc/phim/quanh-ta-la-cac-sao/tap-1/vietsub.m3u8"
          },
          {
            "name": "Tập 2",
            "slug": "tap-2",
            "filename": "tap-2-vietsub",
            "linkEmbed": "https://ophim.cc/api/v9/watch/1001/tap-2",
            "linkM3u8": "https://ophim.cc/phim/quanh-ta-la-cac-sao/tap-2/vietsub.m3u8"
          }
        ]
      },
      {
        "id": 1002,
        "serverName": "EngSub",
        "isAi": false,
        "serverData": [
          {
            "name": "Tập 1",
            "slug": "tap-1",
            "filename": "tap-1-engsub",
            "linkEmbed": "https://ophim.cc/api/v9/watch/1002/tap-1",
            "linkM3u8": "https://ophim.cc/phim/quanh-ta-la-cac-sao/tap-1/engsub.m3u8"
          }
        ]
      }
    ],
    "categories": [...],
    "countries": [...],
    "view": 50000,
    "cinemaScreen": true,
    "actors": ["Ngô Kiến Hào", "Bạch Lộc"],
    "directors": ["Đỗ Lê Hiển"]
  }
}
```

---

### 2️⃣ GET /movies/{id}
**Mục đích**: Lấy chi tiết phim theo ID (giống /movies/detail/{slug})

```bash
curl "http://localhost:8080/movies/1"
```

**Response**: Tương tự endpoint trên

---

### 3️⃣ GET /movies/favorites
**Mục đích**: Lấy danh sách phim yêu thích của user

```bash
curl "http://localhost:8080/movies/favorites?page=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response**:
```json
{
  "status": 200,
  "message": "Lấy danh sách yêu thích thành công",
  "data": [
    {
      "id": 1,
      "name": "Movie 1",
      "episodes": [
        {
          "serverName": "VietSub",
          "serverData": [
            {
              "name": "Tập 1",
              "linkM3u8": "https://..." 
            }
          ]
        }
      ]
    }
  ]
}
```

---

## 💻 FRONTEND EXAMPLES

### Vue.js 3
```vue
<template>
  <div>
    <h1>{{ movie.name }}</h1>
    <video id="video" width="100%" height="auto" controls></video>
    
    <div class="episodes">
      <div v-for="episode in episodes" :key="episode.id">
        <h3>{{ episode.serverName }}</h3>
        <button v-for="server in episode.serverData" 
                :key="server.slug"
                @click="playVideo(server.linkM3u8)">
          {{ server.name }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import Hls from 'hls.js'

export default {
  data() {
    return {
      movie: {},
      episodes: []
    }
  },
  methods: {
    async fetchMovie() {
      const slug = this.$route.params.slug
      const res = await fetch(`/movies/detail/${slug}`)
      const data = await res.json()
      this.movie = data.data
      this.episodes = data.data.episodes
    },
    playVideo(m3u8Link) {
      const video = document.getElementById('video')
      
      if (Hls.isSupported()) {
        const hls = new Hls()
        hls.loadSource(m3u8Link)
        hls.attachMedia(video)
        hls.on(Hls.Events.MANIFEST_PARSED, () => {
          video.play()
        })
      } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = m3u8Link
      }
    }
  },
  mounted() {
    this.fetchMovie()
  }
}
</script>
```

### React
```jsx
import React, { useState, useEffect, useRef } from 'react'
import Hls from 'hls.js'

function MoviePlayer({ slug }) {
  const [movie, setMovie] = useState(null)
  const videoRef = useRef(null)
  const hlsRef = useRef(null)

  useEffect(() => {
    fetchMovie()
  }, [slug])

  const fetchMovie = async () => {
    const res = await fetch(`/movies/detail/${slug}`)
    const data = await res.json()
    setMovie(data.data)
  }

  const playVideo = (m3u8Link) => {
    const video = videoRef.current
    
    if (Hls.isSupported()) {
      if (hlsRef.current) hlsRef.current.destroy()
      
      const hls = new Hls()
      hlsRef.current = hls
      hls.loadSource(m3u8Link)
      hls.attachMedia(video)
      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        video.play()
      })
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = m3u8Link
      video.play()
    }
  }

  if (!movie) return <div>Loading...</div>

  return (
    <div>
      <h1>{movie.name}</h1>
      <video ref={videoRef} width="100%" height="auto" controls />
      
      <div className="episodes">
        {movie.episodes?.map((episode) => (
          <div key={episode.id}>
            <h3>{episode.serverName}</h3>
            {episode.serverData?.map((server) => (
              <button key={server.slug} onClick={() => playVideo(server.linkM3u8)}>
                {server.name}
              </button>
            ))}
          </div>
        ))}
      </div>
    </div>
  )
}

export default MoviePlayer
```

### Vanilla JavaScript
```html
<!DOCTYPE html>
<html>
<head>
  <title>Movie Player</title>
  <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
</head>
<body>
  <h1 id="title"></h1>
  <video id="video" width="100%" controls></video>
  <div id="episodes"></div>

  <script>
    async function loadMovie(slug) {
      const res = await fetch(`/movies/detail/${slug}`)
      const data = await res.json()
      const movie = data.data

      // Set title
      document.getElementById('title').textContent = movie.name

      // Show episodes
      const episodesDiv = document.getElementById('episodes')
      movie.episodes.forEach((episode) => {
        const serverDiv = document.createElement('div')
        serverDiv.innerHTML = `<h3>${episode.serverName}</h3>`
        
        episode.serverData.forEach((server) => {
          const btn = document.createElement('button')
          btn.textContent = server.name
          btn.onclick = () => playVideo(server.linkM3u8)
          serverDiv.appendChild(btn)
        })
        
        episodesDiv.appendChild(serverDiv)
      })
    }

    function playVideo(m3u8Link) {
      const video = document.getElementById('video')
      
      if (Hls.isSupported()) {
        const hls = new Hls()
        hls.loadSource(m3u8Link)
        hls.attachMedia(video)
      } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = m3u8Link
      }
    }

    loadMovie('quanh-ta-la-cac-sao')
  </script>
</body>
</html>
```

### HTML5 Video Tag (Direct)
```html
<video width="100%" height="auto" controls>
  <source src="https://ophim.cc/phim/quanh-ta-la-cac-sao/tap-1/vietsub.m3u8" 
          type="application/x-mpegURL">
  Your browser does not support HLS playback.
</video>
```

---

## 🎯 EXTRACT M3U8 FROM RESPONSE

### JavaScript
```javascript
// Cách 1: Lấy tập đầu tiên, server đầu tiên
const m3u8Link = response.data.episodes[0].serverData[0].linkM3u8

// Cách 2: Lấy tất cả
const allM3u8Links = response.data.episodes.flatMap(ep =>
  ep.serverData.map(sd => ({
    episode: ep.serverName,
    tap: sd.name,
    link: sd.linkM3u8
  }))
)

// Cách 3: Lấy theo server
const vietnameseLinks = response.data.episodes
  .filter(ep => ep.serverName === 'VietSub')
  .flatMap(ep => ep.serverData)
  .map(sd => ({ tap: sd.name, link: sd.linkM3u8 }))
```

---

## 🎬 VIDEO PLAYER LIBRARIES

| Library | Pros | Cons | Docs |
|---------|------|------|------|
| **HLS.js** | Lightweight, Open-source | Need setup | https://github.com/video-dev/hls.js |
| **Video.js** | Full-featured, Popular | Larger size | https://videojs.com |
| **Plyr** | Modern, Easy | Limited features | https://plyr.io |
| **ArtPlayer** | Beautiful UI | Chinese docs | https://artplayer.org |
| **MediaElement.js** | Cross-browser | Older tech | http://www.mediaelementjs.com |

---

## ⚠️ COMMON ISSUES & SOLUTIONS

### Issue 1: CORS Error
```
Error: Failed to load resource: 
No 'Access-Control-Allow-Origin' header
```

**Solution**: M3u8 URL phải có CORS headers từ server gốc. Frontend không thể bypass CORS.

### Issue 2: Stream Not Playing
```javascript
// Check if browser supports HLS
if (Hls.isSupported()) {
  // Use HLS.js
} else if (video.canPlayType('application/vnd.apple.mpegurl')) {
  // Native HLS support (Safari)
  video.src = m3u8Link
} else {
  console.error('HLS not supported')
}
```

### Issue 3: Wrong Server
```javascript
// Tìm server VietSub
const vietnamServer = movie.episodes.find(ep => ep.serverName === 'VietSub')
if (vietnamServer && vietnamServer.serverData.length > 0) {
  playVideo(vietnamServer.serverData[0].linkM3u8)
}
```

---

## 📊 RESPONSE HIERARCHY

```
GET /movies/detail/{slug}
│
└─ data: MovieDetailResponse
   │
   └─ episodes: EpisodeResponse[]
      │
      ├─ [0] serverName: "VietSub"
      │   └─ serverData: ServerDataResponse[]
      │      └─ [0] linkM3u8 ← ✅ M3U8 LINK
      │
      └─ [1] serverName: "EngSub"
          └─ serverData: ServerDataResponse[]
             └─ [0] linkM3u8 ← ✅ M3U8 LINK
```

---

## ✅ COMPLETE CHECKLIST

- [x] API trả về linkM3u8 ở `episodes[].serverData[].linkM3u8`
- [x] EpisodeResponse có field `serverData: ServerDataResponse[]`
- [x] ServerDataResponse có field `linkM3u8: String`
- [x] Có nhiều servers (VietSub, EngSub, etc.)
- [x] Có nhiều tập trong mỗi server
- [x] Frontend có thể chọn server & tập

---

**Status**: ✅ Link M3u8 sẵn sàng để dùng
**Implementation**: Done
**Frontend Support**: Vue, React, Vanilla JS

