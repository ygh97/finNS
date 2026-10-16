<template>
  <p v-if="!posts.length" class="empty-message">소비 내역이 없습니다.</p>
  <div v-else class="table-responsive spending-table">
    <table class="table table-row-bordered align-middle gy-4 gs-4 text-center mb-0">
      <thead>
        <tr class="fw-bold fs-5 text-gray-800">
          <th v-if="isMine">공개</th>
          <th>카테고리</th>
          <th>사용처</th>
          <th>금액</th>
          <th>결제일시</th>
          <th v-if="isMine"></th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="post in posts"
          :key="post.postNo"
          class="fs-6 text-gray-600 fw-bold clickable"
          :class="{ 'bg-gray-100': !post.publicStatus }"
          @click="router.push(`/postView/${post.postNo}`)"
        >
          <td v-if="isMine" @click.stop>
            <div class="form-check form-switch form-check-custom form-check-solid justify-content-center">
              <input
                v-model="post.publicStatus"
                class="form-check-input"
                type="checkbox"
                :aria-label="`${post.place} 공개 여부`"
                @change="togglePublic(post)"
              />
            </div>
          </td>
          <td><img :src="findCategory(post.category).icon" :alt="post.category" width="36" height="36" /></td>
          <td class="text-gray-900">{{ post.place }}</td>
          <td>{{ formatWon(post.amount) }}</td>
          <td>{{ post.transactionDate }}</td>
          <td v-if="isMine">
            <RouterLink :to="`/payDetails/${post.postNo}`" class="btn btn-sm btn-primary text-nowrap" @click.stop>
              소비 상세
            </RouterLink>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import api from '@/api';
import { findCategory } from '@/constants/categories';
import { formatWon } from '@/util/format';

defineProps({
  posts: { type: Array, required: true },
  // 내 소비 내역이면 공개 여부를 바꾸고 상세를 수정할 수 있다
  isMine: { type: Boolean, default: false },
});

const router = useRouter();

const togglePublic = async (post) => {
  try {
    await api.put(`/posts/${post.postNo}/togglePublicStatus`);
  } catch (error) {
    post.publicStatus = !post.publicStatus;
    console.error('공개 여부를 바꾸지 못했습니다.', error);
  }
};
</script>

<style scoped>
.spending-table {
  max-height: 600px;
  overflow-y: auto;
}

.spending-table thead th {
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 1;
}
</style>
