<template>
  <h1>환영합니다!</h1>
  <p class="subtitle">finNS를 이용하려면 로그인해 주세요</p>

  <form @submit.prevent="login(member)">
    <div class="mb-5">
      <label for="username" class="form-label">아이디</label>
      <input id="username" v-model.trim="member.username" type="text" class="form-control" placeholder="아이디를 입력하세요." autocomplete="username" />
    </div>

    <div class="mb-5">
      <label for="password" class="form-label">비밀번호</label>
      <div class="position-relative">
        <input
          id="password"
          v-model="member.password"
          :type="showPassword ? 'text' : 'password'"
          class="form-control pe-12"
          placeholder="비밀번호를 입력하세요."
          autocomplete="current-password"
        />
        <button type="button" class="btn btn-icon btn-sm eye-button" :aria-label="showPassword ? '비밀번호 숨기기' : '비밀번호 보기'" @click="showPassword = !showPassword">
          <i class="ki-duotone fs-2 text-gray-600" :class="showPassword ? 'ki-eye' : 'ki-eye-slash'">
            <span class="path1"></span><span class="path2"></span><span class="path3"></span><span class="path4"></span>
          </i>
        </button>
      </div>
    </div>

    <p v-if="error" class="error-message">{{ error }}</p>
    <button type="submit" class="btn btn-submit mt-2" :disabled="!member.username || !member.password || pending">로그인</button>
  </form>

  <div class="divider"><span>또는</span></div>
  <button type="button" class="btn btn-demo" :disabled="pending" @click="login(DEMO_ACCOUNT)">
    체험 계정으로 둘러보기
  </button>
  <p class="demo-hint">회원가입 없이 예시 데이터가 있는 계정으로 로그인합니다.</p>

  <div class="text-center pt-6">
    <RouterLink to="/auth/join" class="fs-6">회원이 아니신가요? 가입하기</RouterLink>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { DEMO_ACCOUNT, useAuthStore } from '@/stores/auth';

const router = useRouter();
const auth = useAuthStore();

const member = reactive({ username: '', password: '' });
const showPassword = ref(false);
const error = ref('');
const pending = ref(false);

const login = async (account) => {
  pending.value = true;
  error.value = '';
  if (await auth.login(account)) {
    router.push('/');
  } else {
    error.value = '아이디 또는 비밀번호가 올바르지 않습니다.';
  }
  pending.value = false;
};
</script>

<style scoped>
.eye-button {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
}

.error-message {
  color: #fff;
  background-color: rgba(241, 65, 108, 0.85);
  border-radius: 8px;
  padding: 8px 12px;
}

.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 24px 0;
  font-size: 15px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid rgba(255, 255, 255, 0.7);
}

.btn-demo {
  width: 100%;
  height: 50px;
  border-radius: 13px;
  font-size: 17px;
  font-weight: 700;
  background-color: #fff;
  color: #0b8fdb;
}

.btn-demo:hover:not(:disabled) {
  background-color: #eaf6ff;
  color: #0b8fdb;
}

.demo-hint {
  text-align: center;
  font-size: 14px;
  margin: 10px 0 0;
  opacity: 0.9;
}
</style>
