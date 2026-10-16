import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

// 백엔드 경로는 상대경로로 호출 (개발 중에는 vite.config.js 의 proxy가 8080으로 전달)
const instance = axios.create({
  timeout: 30000,
});

// 로그인 토큰(JWT)을 모든 요청에 첨부
instance.interceptors.request.use((config) => {
  const { token } = useAuthStore();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 토큰 만료·무효면 로그아웃 후 로그인 화면으로
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore().logout();
      router.push('/auth/login');
    }
    return Promise.reject(error);
  }
);

export default instance;
