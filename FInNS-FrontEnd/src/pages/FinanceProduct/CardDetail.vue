<template>
  <div v-if="card" class="page-body narrow">
    <header class="text-center mb-10">
      <div class="fs-3 fw-bold text-finns mb-2">{{ card.corpName }}</div>
      <h1 class="fs-2hx fw-bold mb-8">{{ card.cardName }}</h1>
      <img :src="card.imgUrl" alt="" class="card-image" />
    </header>

    <div class="row g-6 mb-10">
      <div class="col-sm-6">
        <div class="soft-box h-100">
          <h3 class="fs-4 fw-bold mb-3">전월 실적</h3>
          <p class="fs-5 mb-0">{{ card.exLMth }}</p>
        </div>
      </div>
      <div class="col-sm-6">
        <div class="soft-box h-100">
          <h3 class="fs-4 fw-bold mb-3">연회비</h3>
          <p v-for="fee in fees" :key="fee" class="fs-5 mb-1">{{ fee }}</p>
        </div>
      </div>
    </div>

    <h3 class="fs-3 fw-bold mb-4">혜택</h3>
    <ul class="list-unstyled">
      <li v-for="[title, detail] in benefits" :key="title" class="benefit-item">
        <strong class="fs-5">{{ title }}</strong>
        <p class="text-gray-700 mb-0">{{ detail }}</p>
      </li>
    </ul>

    <a v-if="card.detailLink" :href="card.detailLink" target="_blank" rel="noopener" class="btn btn-primary mt-8">
      카드사 홈페이지에서 자세히 보기
    </a>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/api';

const route = useRoute();
const card = ref(null);

// 연회비: '국내 1만원 / 해외 1만2천원'
const fees = computed(() => (card.value?.exInFor ? card.value.exInFor.split(' / ') : []));
// 상세 혜택: '제목#설명 | 제목#설명' (설명이 없으면 제목만)
const benefits = computed(() =>
  card.value?.benefitDetail ? card.value.benefitDetail.split(' | ').map((item) => item.split('#')) : []
);

onMounted(async () => {
  try {
    const { data } = await api.get(`/product/card/${route.params.cardNo}`);
    card.value = data;
  } catch (error) {
    console.error('카드 정보를 불러오지 못했습니다.', error);
  }
});
</script>

<style scoped>
.card-image {
  width: 300px;
  max-width: 100%;
}

.benefit-item {
  padding: 14px 0;
  border-bottom: 1px solid #f1f1f4;
}
</style>
