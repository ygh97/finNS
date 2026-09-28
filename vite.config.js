import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// 백엔드(8080)로 전달할 경로 - 프론트 라우트(/card, /profile 등)와 겹치지 않게 유지
const backendPaths = [
  '/api',
  '/users',
  '/posts',
  '/product',
  '/cards',
  '/comment',
  '/follow',
  '/greatOrStupid',
  '/recentUser',
  '/upload',
  // 백엔드 resources에만 있는 이미지 (프론트 public에는 없음)
  '/assets/card',
  '/assets/profile',
  '/assets/peed',
  '/assets/bank',
];

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: Object.fromEntries(backendPaths.map((path) => [path, 'http://localhost:8080'])),
  },
  build: {
    // 백엔드가 정적 파일로 서빙하는 위치 (저장소 기준 상대 경로라 누구 PC에서든 동작)
    outDir: fileURLToPath(new URL('../FInNS-BackEnd/src/main/webapp/resources', import.meta.url)),
    // 비우지 않음: 백엔드 쪽에만 있는 파일(assets/card/*.png 등)이 있음
    emptyOutDir: false,
  },
});
