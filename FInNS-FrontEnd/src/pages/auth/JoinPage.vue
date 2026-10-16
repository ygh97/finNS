<template>
  <h1>회원 가입</h1>
  <p class="subtitle">계정을 만들고 소비 내역을 공유해 보세요.</p>

  <form @submit.prevent="join">
    <div class="mb-5">
      <label for="username" class="form-label">아이디</label>
      <div class="d-flex gap-2">
        <input id="username" v-model.trim="member.username" type="text" class="form-control" placeholder="아이디를 입력하세요." autocomplete="username" @input="usernameChecked = false" />
        <button type="button" class="btn btn-light text-nowrap" :disabled="!member.username" @click="checkUsername">중복 확인</button>
      </div>
      <p v-if="usernameMessage" class="field-message">{{ usernameMessage }}</p>
    </div>

    <div class="mb-5">
      <label for="password" class="form-label">비밀번호</label>
      <input id="password" v-model="member.password" type="password" class="form-control" placeholder="비밀번호를 입력하세요." autocomplete="new-password" />
    </div>

    <div class="mb-5">
      <label for="password2" class="form-label">비밀번호 확인</label>
      <input id="password2" v-model="member.password2" type="password" class="form-control" placeholder="비밀번호를 다시 입력하세요." autocomplete="new-password" />
      <p v-if="member.password2 && member.password !== member.password2" class="field-message">비밀번호가 일치하지 않습니다.</p>
    </div>

    <div class="mb-5">
      <label for="birth" class="form-label">생년월일</label>
      <input id="birth" v-model="member.birth" type="date" class="form-control" />
    </div>

    <button type="submit" class="btn btn-submit mt-4" :disabled="!canSubmit">회원가입</button>
  </form>

  <div class="text-center pt-6">
    <RouterLink to="/auth/login" class="fs-6">이미 계정이 있으신가요? 로그인</RouterLink>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import memberApi from '@/api/memberApi';

const router = useRouter();

const member = reactive({ username: '', password: '', password2: '', birth: '' });
const usernameChecked = ref(false);
const usernameMessage = ref('');

const canSubmit = computed(
  () => usernameChecked.value && member.password && member.password === member.password2 && member.birth
);

const checkUsername = async () => {
  const duplicated = await memberApi.checkUsername(member.username);
  usernameChecked.value = !duplicated;
  usernameMessage.value = duplicated ? '이미 사용 중인 아이디입니다.' : '사용 가능한 아이디입니다.';
};

const join = async () => {
  try {
    await memberApi.join(member);
    alert('회원가입이 완료되었습니다. 로그인해 주세요.');
    router.push('/auth/login');
  } catch {
    alert('회원가입에 실패했습니다.');
  }
};
</script>

<style scoped>
.field-message {
  font-size: 14px;
  margin: 6px 0 0 4px;
}
</style>
