<template>
  <button
    class="btn btn-sm flex-shrink-0"
    :class="following ? 'btn-light' : 'btn-primary'"
    :disabled="pending"
    @click="toggle"
  >
    {{ following ? '팔로잉' : '팔로우' }}
  </button>
</template>

<script setup>
import { ref, watch } from 'vue';
import api from '@/api';

const props = defineProps({
  userNo: { type: Number, required: true },
  initialFollowing: { type: Boolean, default: false },
});

const following = ref(props.initialFollowing);
const pending = ref(false);
watch(() => props.initialFollowing, (value) => (following.value = value));

// 팔로우하는 사람은 서버가 토큰으로 판단
const toggle = async () => {
  pending.value = true;
  try {
    if (following.value) await api.delete(`/follow/${props.userNo}`);
    else await api.post(`/follow/${props.userNo}`);
    following.value = !following.value;
  } catch (error) {
    console.error('팔로우 변경 실패', error);
  } finally {
    pending.value = false;
  }
};
</script>
