<template>
  <div class="follow-list">
    <p v-if="!users.length" class="empty-message">{{ type === 'follower' ? '팔로워가' : '팔로잉한 회원이' }} 없습니다.</p>
    <div v-for="user in users" :key="user.user_no" class="d-flex align-items-center gap-4 py-4 border-bottom border-gray-300 border-bottom-dashed">
      <img :src="user.img_url" class="avatar" width="40" height="40" alt="" />
      <div class="flex-grow-1">
        <RouterLink :to="`/profile/${user.user_no}/spending`" class="fs-5 fw-bold text-gray-900 text-hover-primary">
          {{ user.username }}
        </RouterLink>
        <div class="fw-semibold text-muted">{{ user.mbti_name || 'MBTI 진단 전' }}</div>
      </div>
      <FollowButton v-if="user.user_no !== auth.userNo" :user-no="user.user_no" :initial-following="user.following" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import FollowButton from '@/components/common/FollowButton.vue';

// follower: 나를 팔로우하는 회원, following: 내가 팔로우하는 회원
const props = defineProps({
  type: { type: String, required: true },
});

const route = useRoute();
const auth = useAuthStore();
const users = ref([]);

onMounted(async () => {
  try {
    const { data } = await api.get(`/users/${route.params.userNo}/${props.type}`);
    users.value = data;
  } catch (error) {
    console.error('팔로우 목록을 불러오지 못했습니다.', error);
  }
});
</script>

<style scoped>
.follow-list {
  max-width: 700px;
  margin: 0 auto;
}
</style>
