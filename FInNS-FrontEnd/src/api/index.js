import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

// 백엔드 경로는 상대경로로 호출 (개발 시 vite.config.js의 proxy가 8080으로 전달)
const instance = axios.create({
  timeout: 30000,
});

// 요청 인터셉터 - 로그인 토큰(JWT)을 모든 요청에 첨부
instance.interceptors.request.use((config) => {
  const token = useAuthStore().getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터 - 토큰 만료/무효 시 로그인 페이지로
instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const { logout } = useAuthStore();
      logout();
      router.push('/auth/login?error=login_required');
      return Promise.reject({ error: '로그인이 필요한 서비스입니다.' });
    }
    return Promise.reject(error);
  }
);

export default instance; // 인터셉터가 적용된 axios 인스턴스
