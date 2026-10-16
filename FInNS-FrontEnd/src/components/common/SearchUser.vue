<template>
  <div ref="root" class="search-user">
    <div class="position-relative">
      <!-- v-model은 한글 입력(IME) 조합이 끝나야 값이 바뀌어서 input 이벤트로 직접 받는다 -->
      <input
        :value="query"
        type="text"
        class="form-control form-control-solid pe-12"
        placeholder="유저 이름을 검색하세요"
        @input="query = $event.target.value.trim()"
        @focus="open"
        @keydown.esc="isOpen = false"
      />
      <i class="ki-duotone ki-magnifier fs-2 text-gray-500 search-icon">
        <span class="path1"></span><span class="path2"></span>
      </i>
    </div>

    <div v-if="isOpen" class="search-dropdown">
      <template v-if="query">
        <h4 class="list-title">검색된 유저</h4>
        <p v-if="!results.length" class="text-center text-danger my-6">검색 결과가 없습니다.</p>
      </template>
      <h4 v-else class="list-title">최근 본 유저</h4>

      <ul class="list-unstyled mb-0">
        <li v-for="user in query ? results : recentUsers" :key="user.userNo" class="user-row">
          <img :src="user.imgUrl" class="avatar" width="36" height="36" alt="" />
          <a class="flex-grow-1 fs-5 fw-bold text-gray-900 text-hover-primary clickable" @click="goToProfile(user.userNo)">
            {{ user.userName }}
          </a>
          <FollowButton v-if="user.userNo !== auth.userNo" :user-no="user.userNo" :initial-following="user.follow" />
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import FollowButton from './FollowButton.vue';

const router = useRouter();
const auth = useAuthStore();

const root = ref(null);
const query = ref('');
const isOpen = ref(false);
const users = ref([]);
const recentUsers = ref([]);

// 회원 수가 적어서 전체 목록을 받아 화면에서 거른다
const results = computed(() => {
  const keyword = query.value.toLowerCase();
  return users.value.filter((user) => user.userName.toLowerCase().includes(keyword));
});

const open = async () => {
  isOpen.value = true;
  try {
    const [all, recent] = await Promise.all([api.get('/users/search'), api.get('/recentUser')]);
    users.value = all.data;
    recentUsers.value = recent.data;
  } catch (error) {
    console.error('유저 목록을 불러오지 못했습니다.', error);
  }
};

const goToProfile = async (userNo) => {
  isOpen.value = false;
  await api.put(`/recentUser/${userNo}`).catch(() => {});
  router.push(`/profile/${userNo}/spending`);
};

const closeOnOutsideClick = (event) => {
  if (root.value && !root.value.contains(event.target)) isOpen.value = false;
};
onMounted(() => document.addEventListener('click', closeOnOutsideClick));
onBeforeUnmount(() => document.removeEventListener('click', closeOnOutsideClick));
</script>

<style scoped>
.search-user {
  position: relative;
  width: 100%;
  max-width: 500px;
}

.search-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
}

.search-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  z-index: 50;
  max-height: 420px;
  overflow-y: auto;
  padding: 8px 16px 12px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.list-title {
  color: #7b7777;
  font-size: 1.05rem;
  margin: 12px 0 8px;
}

.user-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
</style>
