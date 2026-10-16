<template>
  <Banner title="소비 상세" description="소비 내역을 확인하고 공개 범위·카테고리·메모를 수정할 수 있습니다." />

  <form v-if="post" class="page-body narrow" @submit.prevent="save">
    <dl class="info-list">
      <div><dt>사용처</dt><dd>{{ post.place }}</dd></div>
      <div><dt>사용 금액</dt><dd>{{ formatWon(post.amount) }}</dd></div>
      <div><dt>결제 일시</dt><dd>{{ post.transactionDate }}</dd></div>
      <div><dt>결제 수단</dt><dd>{{ post.cardName }}</dd></div>
    </dl>

    <Carousel v-if="post.imgUrls?.length" class="mb-8">
      <Slide v-for="image in post.imgUrls" :key="image">
        <img :src="image" alt="" class="post-image" />
      </Slide>
      <template #addons>
        <Navigation v-if="post.imgUrls.length > 1" />
        <Pagination v-if="post.imgUrls.length > 1" />
      </template>
    </Carousel>

    <fieldset class="mb-8">
      <legend class="form-label">공개 범위</legend>
      <div class="d-flex gap-6">
        <label v-for="option in visibility" :key="option.label" class="form-check form-check-custom form-check-solid">
          <input v-model="post.publicStatus" class="form-check-input" type="radio" :value="option.value" />
          <span class="form-check-label fs-5" :class="{ 'text-finns fw-bold': post.publicStatus === option.value }">
            {{ option.label }}
          </span>
        </label>
      </div>
    </fieldset>

    <div class="mb-8">
      <label for="category" class="form-label">카테고리</label>
      <select id="category" v-model="post.category" class="form-select form-select-solid">
        <option v-for="category in CATEGORIES" :key="category.name" :value="category.name">{{ category.name }}</option>
      </select>
    </div>

    <div class="mb-10">
      <label for="memo" class="form-label">메모</label>
      <textarea id="memo" v-model="post.memo" class="form-control form-control-solid" rows="3" placeholder="메모를 입력하세요"></textarea>
    </div>

    <div class="d-flex gap-4">
      <button type="button" class="btn btn-light flex-fill" @click="router.back()">취소</button>
      <button type="submit" class="btn btn-primary flex-fill" :disabled="saving">저장</button>
    </div>
  </form>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import 'vue3-carousel/dist/carousel.css';
import { Carousel, Navigation, Pagination, Slide } from 'vue3-carousel';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import { CATEGORIES } from '@/constants/categories';
import { formatWon } from '@/util/format';
import Banner from '@/components/common/Banner.vue';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const postNo = route.params.postNo;

const visibility = [
  { label: '전체 공개', value: true },
  { label: '비공개', value: false },
];

const post = ref(null);
const saving = ref(false);

onMounted(async () => {
  try {
    const { data } = await api.get(`/posts/${postNo}`);
    // 다른 사람의 글은 보기 화면으로
    if (data.userNo !== auth.userNo) return router.replace(`/postView/${postNo}`);
    post.value = data;
  } catch (error) {
    console.error('소비 내역을 불러오지 못했습니다.', error);
  }
});

// 본인 글만 수정 가능 (서버가 토큰으로 확인)
const save = async () => {
  saving.value = true;
  try {
    const { publicStatus, category, memo } = post.value;
    await api.put(`/posts/${postNo}/update`, { publicStatus, category, memo });
    router.push(`/postView/${postNo}`);
  } catch {
    alert('저장에 실패했습니다.');
  } finally {
    saving.value = false;
  }
};
</script>

<style scoped>
.info-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  padding: 20px;
  margin-bottom: 32px;
  border-radius: 12px;
  background-color: var(--finns-soft);
}

.info-list dt {
  color: #5e6278;
  font-size: 0.95rem;
  margin-bottom: 4px;
}

.info-list dd {
  font-size: 1.15rem;
  font-weight: 700;
  margin: 0;
}

.form-label {
  font-size: 1.2rem;
  font-weight: 700;
  color: #3f4254;
}

.post-image {
  width: 100%;
  height: 360px;
  object-fit: contain;
}
</style>
