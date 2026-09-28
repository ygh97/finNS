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
  // 빌드 결과물은 dist/ (저장소에 올리지 않음). 백엔드 WAR를 만들 때 WAR의 /resources 에 합쳐진다 (FInNS-BackEnd/build.gradle)
});
