#!/usr/bin/env node

/**
 * Complete Example: How to Get M3U8 Link and Play Video
 *
 * This example shows the complete flow:
 * 1. Fetch movie detail from API
 * 2. Extract M3U8 link
 * 3. Use in video player
 */

// ============================================
// EXAMPLE 1: Vanilla JavaScript (HTML5)
// ============================================

async function example1_VanillaJS() {
  // 1. Fetch movie detail
  const response = await fetch('/movies/detail/quanh-ta-la-cac-sao');
  const data = await response.json();
  const movie = data.data;

  console.log('Movie:', movie.name);
  console.log('Số tập:', movie.episodes.length);

  // 2. Get first episode, first server
  const firstEpisode = movie.episodes[0];  // VietSub
  const firstServer = firstEpisode.serverData[0];  // Tập 1
  const m3u8Link = firstServer.linkM3u8;

  console.log(`Playing: ${firstEpisode.serverName} - ${firstServer.name}`);
  console.log('M3U8 Link:', m3u8Link);

  // 3. Set video source
  const video = document.getElementById('video');
  video.src = m3u8Link;
}

// ============================================
// EXAMPLE 2: Get All Episodes List
// ============================================

async function example2_GetAllEpisodes(slug) {
  const response = await fetch(`/movies/detail/${slug}`);
  const data = await response.json();
  const movie = data.data;

  // Build episodes list
  const episodes = [];

  movie.episodes.forEach((episode, episodeIdx) => {
    episode.serverData.forEach((server, serverIdx) => {
      episodes.push({
        serverName: episode.serverName,
        episodeName: server.name,
        m3u8Link: server.linkM3u8,
        linkEmbed: server.linkEmbed,
        indices: {
          episode: episodeIdx,
          server: serverIdx
        }
      });
    });
  });

  return episodes;
}

// Usage:
// const episodes = await example2_GetAllEpisodes('quanh-ta-la-cac-sao');
// episodes.forEach(ep => console.log(`${ep.serverName} - ${ep.episodeName}: ${ep.m3u8Link}`));

// ============================================
// EXAMPLE 3: Select by Server Name
// ============================================

async function example3_SelectServer(slug, serverName = 'VietSub') {
  const response = await fetch(`/movies/detail/${slug}`);
  const data = await response.json();
  const movie = data.data;

  // Find server
  const episode = movie.episodes.find(ep => ep.serverName === serverName);

  if (!episode) {
    console.error(`Server ${serverName} not found`);
    return null;
  }

  // Get all episodes from this server
  return episode.serverData.map(server => ({
    name: server.name,
    m3u8Link: server.linkM3u8
  }));
}

// Usage:
// const vietnamEpisodes = await example3_SelectServer('quanh-ta-la-cac-sao', 'VietSub');
// vietnamEpisodes.forEach(ep => console.log(`${ep.name}: ${ep.m3u8Link}`));

// ============================================
// EXAMPLE 4: HLS.js Player Implementation
// ============================================

async function example4_HLSPlayer(slug, episodeIdx = 0, serverIdx = 0) {
  // 1. Fetch data
  const response = await fetch(`/movies/detail/${slug}`);
  const data = await response.json();
  const movie = data.data;

  // 2. Get m3u8 link
  const episode = movie.episodes[episodeIdx];
  const server = episode.serverData[serverIdx];
  const m3u8Link = server.linkM3u8;

  // 3. Setup HLS
  const video = document.getElementById('video');

  if (Hls.isSupported()) {
    const hls = new Hls();
    hls.loadSource(m3u8Link);
    hls.attachMedia(video);

    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      console.log('Manifest parsed, starting playback');
      video.play();
    });

    hls.on(Hls.Events.ERROR, (event, data) => {
      if (data.fatal) {
        console.error('Fatal error:', data.type);
        switch (data.type) {
          case Hls.ErrorTypes.NETWORK_ERROR:
            hls.startLoad();
            break;
          case Hls.ErrorTypes.MEDIA_ERROR:
            hls.recoverMediaError();
            break;
        }
      }
    });
  } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
    // Native HLS support (Safari)
    video.src = m3u8Link;
    video.play();
  }
}

// Usage:
// <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
// example4_HLSPlayer('quanh-ta-la-cac-sao', 0, 0);

// ============================================
// EXAMPLE 5: Vue Component
// ============================================

const VueMoviePlayer = {
  template: `
    <div class="movie-player">
      <h1>{{ movie.name }}</h1>

      <video id="video" width="100%" height="auto" controls></video>

      <div class="controls">
        <div class="servers">
          <h3>Server:</h3>
          <button v-for="(ep, idx) in movie.episodes" :key="idx"
                  :class="{ active: selectedServer === idx }"
                  @click="selectServer(idx)">
            {{ ep.serverName }}
          </button>
        </div>

        <div class="episodes">
          <h3>Tập:</h3>
          <button v-for="(server, idx) in currentEpisodes" :key="idx"
                  @click="playEpisode(idx)">
            {{ server.name }}
          </button>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      movie: {},
      selectedServer: 0,
      slug: ''
    };
  },
  computed: {
    currentEpisodes() {
      return this.movie.episodes?.[this.selectedServer]?.serverData || [];
    }
  },
  methods: {
    async loadMovie() {
      const res = await fetch(`/movies/detail/${this.slug}`);
      const data = await res.json();
      this.movie = data.data;
    },
    selectServer(idx) {
      this.selectedServer = idx;
    },
    playEpisode(serverIdx) {
      const m3u8Link = this.movie.episodes[this.selectedServer].serverData[serverIdx].linkM3u8;

      const video = document.getElementById('video');
      if (Hls.isSupported()) {
        const hls = new Hls();
        hls.loadSource(m3u8Link);
        hls.attachMedia(video);
      }
    }
  },
  mounted() {
    this.slug = this.$route.params.slug;
    this.loadMovie();
  }
};

// ============================================
// EXAMPLE 6: React Hook
// ============================================

/*
import { useState, useEffect, useRef } from 'react';
import Hls from 'hls.js';

function MoviePlayer({ slug }) {
  const [movie, setMovie] = useState(null);
  const [selectedServer, setSelectedServer] = useState(0);
  const videoRef = useRef(null);

  useEffect(() => {
    fetchMovie();
  }, [slug]);

  const fetchMovie = async () => {
    const res = await fetch(`/movies/detail/${slug}`);
    const data = await res.json();
    setMovie(data.data);
  };

  const playEpisode = (serverIdx) => {
    const m3u8Link = movie.episodes[selectedServer].serverData[serverIdx].linkM3u8;

    const video = videoRef.current;
    if (Hls.isSupported()) {
      const hls = new Hls();
      hls.loadSource(m3u8Link);
      hls.attachMedia(video);
    }
  };

  if (!movie) return <div>Loading...</div>;

  return (
    <div>
      <h1>{movie.name}</h1>
      <video ref={videoRef} width="100%" controls />

      <div>
        {movie.episodes.map((ep, idx) => (
          <button key={idx} onClick={() => setSelectedServer(idx)}>
            {ep.serverName}
          </button>
        ))}
      </div>

      <div>
        {movie.episodes[selectedServer]?.serverData.map((server, idx) => (
          <button key={idx} onClick={() => playEpisode(idx)}>
            {server.name}
          </button>
        ))}
      </div>
    </div>
  );
}

export default MoviePlayer;
*/

// ============================================
// EXAMPLE 7: Complete HTML Page
// ============================================

const HTMLPage = `
<!DOCTYPE html>
<html>
<head>
  <title>Movie Player</title>
  <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
  <style>
    body {
      font-family: Arial, sans-serif;
      max-width: 1000px;
      margin: 0 auto;
      padding: 20px;
    }

    #video {
      width: 100%;
      height: auto;
      margin: 20px 0;
      background: #000;
    }

    .controls {
      margin: 20px 0;
    }

    .control-group {
      margin: 10px 0;
    }

    button {
      padding: 8px 16px;
      margin: 5px;
      cursor: pointer;
      background: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
    }

    button.active {
      background: #dc3545;
    }

    button:hover {
      opacity: 0.8;
    }
  </style>
</head>
<body>
  <h1 id="title">Loading...</h1>
  <video id="video" controls></video>

  <div class="controls">
    <div class="control-group">
      <h3>Server:</h3>
      <div id="servers"></div>
    </div>

    <div class="control-group">
      <h3>Episodes:</h3>
      <div id="episodes"></div>
    </div>
  </div>

  <script>
    let movie = null;
    let selectedServer = 0;

    async function loadMovie(slug) {
      const res = await fetch(\`/movies/detail/\${slug}\`);
      const data = await res.json();
      movie = data.data;

      document.getElementById('title').textContent = movie.name;
      renderServers();
      renderEpisodes();
    }

    function renderServers() {
      const container = document.getElementById('servers');
      container.innerHTML = '';

      movie.episodes.forEach((ep, idx) => {
        const btn = document.createElement('button');
        btn.textContent = ep.serverName;
        btn.className = idx === selectedServer ? 'active' : '';
        btn.onclick = () => selectServer(idx);
        container.appendChild(btn);
      });
    }

    function renderEpisodes() {
      const container = document.getElementById('episodes');
      container.innerHTML = '';

      const currentEpisode = movie.episodes[selectedServer];
      currentEpisode.serverData.forEach((server, idx) => {
        const btn = document.createElement('button');
        btn.textContent = server.name;
        btn.onclick = () => playEpisode(idx);
        container.appendChild(btn);
      });
    }

    function selectServer(idx) {
      selectedServer = idx;
      renderServers();
      renderEpisodes();
    }

    function playEpisode(serverIdx) {
      const m3u8Link = movie.episodes[selectedServer].serverData[serverIdx].linkM3u8;
      const video = document.getElementById('video');

      if (Hls.isSupported()) {
        const hls = new Hls();
        hls.loadSource(m3u8Link);
        hls.attachMedia(video);
      } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = m3u8Link;
      }
    }

    // Load movie
    loadMovie('quanh-ta-la-cac-sao');
  </script>
</body>
</html>
`;

// ============================================
// HELPER FUNCTION: Build Episode List
// ============================================

function buildEpisodeList(movie) {
  const list = [];

  movie.episodes.forEach((episode, episodeIdx) => {
    episode.serverData.forEach((server, serverIdx) => {
      list.push({
        serverName: episode.serverName,
        isAi: episode.isAi,
        episodeName: server.name,
        episodeSlug: server.slug,
        m3u8Link: server.linkM3u8,
        embedLink: server.linkEmbed,
        position: {
          episode: episodeIdx,
          server: serverIdx
        }
      });
    });
  });

  return list;
}

// Usage:
// const list = buildEpisodeList(movieData.data);
// list.forEach(item => console.log(item));

module.exports = {
  example1_VanillaJS,
  example2_GetAllEpisodes,
  example3_SelectServer,
  example4_HLSPlayer,
  buildEpisodeList
};

