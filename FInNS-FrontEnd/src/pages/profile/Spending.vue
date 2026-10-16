<template>
  <Calendar :user-no="userNo" @select="loadPostsOn" />
  <h3 class="fs-3 fw-bold mt-10 mb-4">{{ selectedDate }} 소비 내역</h3>
  <SpendingTable :posts="posts" :is-mine="isMine" />
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import Calendar from '@/components/common/Calendar.vue';
import SpendingTable from '@/components/common/SpendingTable.vue';

const route = useRoute();
const auth = useAuthStore();
const userNo = Number(route.params.userNo);
const isMine = computed(() => userNo === auth.userNo);

const selectedDate = ref('');
const posts = ref([]);

const loadPostsOn = async (date) => {
  selectedDate.value = date;
  posts.value = [];
  try {
    // 다른 사람의 소비 내역은 공개 글만
    const { data } = await api.post('/posts/byDate', { userNo, date, isOnlyPublic: !isMine.value });
    posts.value = data;
  } catch (error) {
    console.error('소비 내역을 불러오지 못했습니다.', error);
  }
};
</script>
