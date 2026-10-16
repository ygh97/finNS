<template>
  <Banner title="프로필 수정" description="프로필 사진과 비밀번호를 바꿀 수 있습니다." />

  <form class="page-body narrow" @submit.prevent="save">
    <div v-if="auth.isDemo" class="alert alert-warning mb-8">체험 계정은 정보를 수정할 수 없습니다.</div>

    <div class="d-flex align-items-center gap-6 mb-10">
      <img :src="avatarPreview" class="avatar" width="120" height="120" alt="프로필 사진" />
      <div>
        <label class="btn btn-light-primary btn-sm">
          사진 변경
          <input type="file" accept="image/png,image/jpeg" class="d-none" @change="onAvatarChange" />
        </label>
        <p class="text-gray-500 fs-7 mt-2 mb-0">PNG, JPG 파일 (최대 10MB)</p>
      </div>
    </div>

    <div class="row mb-6">
      <label class="col-sm-4 col-form-label fw-bold">아이디</label>
      <div class="col-sm-8"><input type="text" class="form-control form-control-solid" :value="auth.user?.username" readonly /></div>
    </div>
    <div class="row mb-6">
      <label class="col-sm-4 col-form-label fw-bold">생년월일</label>
      <div class="col-sm-8"><input type="text" class="form-control form-control-solid" :value="formatDate(auth.user?.birth)" readonly /></div>
    </div>
    <div class="row mb-6">
      <label for="oldPassword" class="col-sm-4 col-form-label fw-bold">현재 비밀번호</label>
      <div class="col-sm-8">
        <input id="oldPassword" v-model="passwords.old" type="password" class="form-control" autocomplete="current-password" />
      </div>
    </div>
    <div class="row mb-6">
      <label for="newPassword" class="col-sm-4 col-form-label fw-bold">새 비밀번호</label>
      <div class="col-sm-8">
        <input id="newPassword" v-model="passwords.new" type="password" class="form-control" autocomplete="new-password" />
      </div>
    </div>
    <div class="row mb-6">
      <label for="newPassword2" class="col-sm-4 col-form-label fw-bold">새 비밀번호 확인</label>
      <div class="col-sm-8">
        <input id="newPassword2" v-model="passwords.new2" type="password" class="form-control" autocomplete="new-password" />
      </div>
    </div>

    <p v-if="error" class="text-danger text-end">{{ error }}</p>
    <div class="d-flex justify-content-end gap-2 mt-8">
      <RouterLink :to="`/profile/${auth.userNo}`" class="btn btn-light">취소</RouterLink>
      <button type="submit" class="btn btn-primary" :disabled="auth.isDemo || saving">저장</button>
    </div>
  </form>
</template>

<script setup>
import { reactive, ref } from 'vue';
import memberApi from '@/api/memberApi';
import { useAuthStore } from '@/stores/auth';
import { formatDate } from '@/util/format';
import Banner from '@/components/common/Banner.vue';

const auth = useAuthStore();

const avatarFile = ref(null);
const avatarPreview = ref(auth.user?.img_url || '/assets/media/avatars/blank.png');
const passwords = reactive({ old: '', new: '', new2: '' });
const error = ref('');
const saving = ref(false);

const onAvatarChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  avatarFile.value = file;
  avatarPreview.value = URL.createObjectURL(file);
};

const save = async () => {
  if (passwords.new !== passwords.new2) return (error.value = '새 비밀번호가 일치하지 않습니다.');
  if (passwords.new && !passwords.old) return (error.value = '현재 비밀번호를 입력해 주세요.');
  if (!passwords.new && !avatarFile.value) return (error.value = '바꿀 내용이 없습니다.');

  const formData = new FormData();
  formData.append('oldPassword', passwords.old);
  formData.append('newPassword', passwords.new);
  if (avatarFile.value) formData.append('avatar', avatarFile.value);

  saving.value = true;
  try {
    const updated = await memberApi.update(auth.username, formData);
    auth.changeProfile(updated);
    Object.assign(passwords, { old: '', new: '', new2: '' });
    avatarFile.value = null;
    error.value = '';
    alert('정보를 수정했습니다.');
  } catch (e) {
    // 서버 오류 응답은 text/plain 메시지
    const message = e.response?.data;
    error.value = typeof message === 'string' && message ? message : '수정에 실패했습니다.';
  } finally {
    saving.value = false;
  }
};
</script>
