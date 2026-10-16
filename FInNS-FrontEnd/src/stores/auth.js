import { computed, ref } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';

const STORAGE_KEY = 'auth';

// 배포 사이트에서 회원가입 없이 둘러볼 수 있는 체험 계정 (db/seed.sql)
export const DEMO_ACCOUNT = { username: 'demo', password: '1234' };

const loadSaved = () => {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) ?? {};
  } catch {
    return {};
  }
};

export const useAuthStore = defineStore('auth', () => {
  const saved = loadSaved();
  const token = ref(saved.token ?? '');
  // 서버 로그인 응답의 user: { user_no, username, birth, mbti_name, img_url, renew_time }
  const user = ref(saved.user ?? null);

  const isLogin = computed(() => !!token.value);
  const userNo = computed(() => user.value?.user_no);
  const username = computed(() => user.value?.username);
  const isDemo = computed(() => username.value === DEMO_ACCOUNT.username);

  const save = () => localStorage.setItem(STORAGE_KEY, JSON.stringify({ token: token.value, user: user.value }));

  // 로그인 API는 인증 헤더가 필요 없어서 기본 axios를 쓴다
  const login = async (member) => {
    try {
      const { data } = await axios.post('/api/auth/login', member);
      token.value = data.token;
      user.value = data.user;
      save();
      return true;
    } catch {
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem(STORAGE_KEY);
    token.value = '';
    user.value = null;
  };

  // 프로필 수정 후 서버가 돌려준 회원 정보 반영
  const changeProfile = (member) => {
    if (member.img_url) user.value = { ...user.value, img_url: member.img_url };
    save();
  };

  return { token, user, isLogin, userNo, username, isDemo, login, logout, changeProfile };
});
