<template>
  <Banner title="카드 상품" description="혜택 분야를 골라 나에게 맞는 카드 상품을 확인해보세요." />

  <div class="page-body">
    <FinanceTabs />

    <section class="card card-body shadow-sm mb-8">
      <label class="form-label fw-bold" for="keyword">검색</label>
      <div class="position-relative search-box">
        <i class="ki-duotone ki-magnifier fs-3 text-gray-500 position-absolute top-50 translate-middle-y ms-4">
          <span class="path1"></span><span class="path2"></span>
        </i>
        <input id="keyword" v-model.trim="keyword" type="text" class="form-control form-control-solid ps-12"
          placeholder="카드사, 카드명을 입력해주세요." />
      </div>
    </section>

    <div class="d-flex flex-wrap gap-2 mb-6">
      <button v-for="tab in TABS" :key="tab" type="button" class="btn btn-sm fw-bold"
        :class="selectedTab === tab ? 'btn-primary' : 'btn-light'" @click="selectedTab = tab">
        {{ tab }}
      </button>
    </div>

    <h3 class="fw-bold mb-4">{{ filtered.length }} <span class="text-gray-500 fs-6">개 카드</span></h3>

    <RouterLink v-for="card in paged" :key="card.cardNo" :to="`/card/${card.cardNo}`" class="card card-flush shadow-sm card-row mb-5">
      <img :src="card.imgUrl" alt="" class="card-image" />
      <div class="flex-grow-1">
        <div class="text-gray-500 fw-semibold">{{ card.corpName }}</div>
        <div class="fs-2 fw-bold text-gray-900 mb-4">{{ card.cardName }}</div>
        <div class="d-flex flex-wrap gap-3">
          <div v-for="[title, detail] in benefitsOf(card)" :key="title" class="benefit">
            <div class="fs-5 fw-bold text-gray-800">{{ title }}</div>
            <div class="fs-7 fw-semibold text-gray-500">{{ detail }}</div>
          </div>
        </div>
      </div>
    </RouterLink>
    <p v-if="loaded && !filtered.length" class="empty-message">조건에 맞는 카드가 없습니다.</p>

    <Pagination v-model:page="page" v-model:page-size="pageSize" :total="filtered.length" :page-sizes="[5, 10, 20]" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import api from '@/api';
import Banner from '@/components/common/Banner.vue';
import FinanceTabs from '@/components/common/FinanceTabs.vue';
import Pagination from '@/components/common/Pagination.vue';

// 카드 혜택 분야 - DB card_type.type 과 같아야 한다 (CardService.matchingCategory)
const TABS = ['전체', '식비', '여가', '교통', '의료', '통신', '교육'];

const cards = ref([]);
const loaded = ref(false);
const keyword = ref('');
const selectedTab = ref('전체');
const page = ref(1);
const pageSize = ref(5);

const filtered = computed(() =>
  cards.value.filter(
    (card) =>
      (card.cardName.includes(keyword.value) || card.corpName.includes(keyword.value)) &&
      (selectedTab.value === '전체' || card.cardCategories.split(',').map((c) => c.trim()).includes(selectedTab.value))
  )
);

const paged = computed(() => filtered.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value));
watch([keyword, selectedTab], () => (page.value = 1));

// 요약 혜택: '제목#설명 | 제목#설명'
const benefitsOf = (card) => (card.benefit ? card.benefit.split(' | ').map((item) => item.split('#')) : []);

onMounted(async () => {
  try {
    const { data } = await api.get('/product/card');
    cards.value = data;
  } catch (error) {
    console.error('카드 목록을 불러오지 못했습니다.', error);
  } finally {
    loaded.value = true;
  }
});
</script>

<style scoped>
.search-box {
  max-width: 600px;
}

.card-row {
  flex-direction: row;
  align-items: center;
  gap: 32px;
  padding: 24px 32px;
  transition: box-shadow 0.15s ease;
}

.card-row:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1) !important;
}

.card-image {
  width: 180px;
  flex-shrink: 0;
  object-fit: contain;
}

.benefit {
  min-width: 140px;
  padding: 10px 14px;
  border: 1px dashed #e1e3ea;
  border-radius: 8px;
}

@media (max-width: 767.98px) {
  .card-row {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
}
</style>
