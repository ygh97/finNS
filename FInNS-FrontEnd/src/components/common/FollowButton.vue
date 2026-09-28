<template>
  <button
    class="btn btn-sm ms-10"
    :class="isFollowing ? 'btn-light' : 'btn-primary'"
    @click="followClick"
  >
    <span>{{ isFollowing ? '팔로잉' : '팔로우' }}</span>
  </button>
</template>

<script setup>
import { ref, watch } from 'vue';
import api from '@/api';

const props = defineProps({
  to_user_no: {
    type: Number,
    required: true,
  },
  initialIsFollowing: {
    type: Boolean,
    required: true,
  },
});

const isFollowing = ref(props.initialIsFollowing);

// `props.initialIsFollowing`의 변화를 감지해서 `isFollowing`을 업데이트
watch(() => props.initialIsFollowing, (newVal) => {
  isFollowing.value = newVal;
});

// 팔로우하는 사람은 서버가 토큰으로 판단
const followClick = async () => {
  try {
    if (isFollowing.value) {
      await api.delete(`/follow/${props.to_user_no}`);
    } else {
      await api.post(`/follow/${props.to_user_no}`);
    }

    // 팔로우 상태 변경
    isFollowing.value = !isFollowing.value;

  } catch (error) {
    console.error('팔로우/언팔로우 요청 실패:', error);
  }
};
</script>
