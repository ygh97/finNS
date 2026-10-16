<template>
  <p v-if="!categories.length" class="empty-message">분석할 소비 내역이 없습니다.</p>
  <template v-else>
    <div class="row g-10 align-items-center">
      <!-- 카테고리별 비율 도넛 차트 -->
      <div class="col-lg-5 d-flex flex-column align-items-center">
        <div class="donut" :style="{ background: donutGradient }" role="img" aria-label="카테고리별 소비 비율">
          <div class="donut-hole">
            <span class="fs-7 text-gray-600">총 소비</span>
            <span class="fs-3 fw-bold text-gray-900">{{ formatWon(total) }}</span>
          </div>
        </div>
      </div>

      <div class="col-lg-7">
        <ul class="list-unstyled mb-0">
          <li
            v-for="item in categories"
            :key="item.category"
            class="category-row clickable"
            :class="{ selected: item.category === selected }"
            :style="{ backgroundColor: findCategory(item.category).color }"
            @click="select(item.category)"
          >
            <img :src="findCategory(item.category).icon" alt="" width="36" height="36" />
            <span class="flex-grow-1 fs-4 fw-bold">{{ item.category }}</span>
            <span class="fs-6 fw-semibold me-4">{{ formatWon(item.amount) }}</span>
            <span class="fs-4 fw-bold">{{ item.percentage }}%</span>
          </li>
        </ul>
      </div>
    </div>

    <h3 class="fs-3 fw-bold mt-12 mb-4">{{ selected }} 소비 내역</h3>
    <SpendingTable :posts="posts" :is-mine="isMine" />
  </template>

  <Banner title="추천 카드" description="소비 내역을 분석해서 골라봤어요." class="mt-14 mb-10" />
  <div class="d-flex flex-wrap justify-content-center gap-8">
    <CardComponent v-for="card in cards" :key="card.cardNo" :card="card" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import { findCategory } from '@/constants/categories';
import { formatWon } from '@/util/format';
import Banner from '@/components/common/Banner.vue';
import CardComponent from '@/components/common/CardComponent.vue';
import SpendingTable from '@/components/common/SpendingTable.vue';

const route = useRoute();
const auth = useAuthStore();
const userNo = Number(route.params.userNo);
const isMine = computed(() => userNo === auth.userNo);

const amounts = ref([]);
const selected = ref('');
const posts = ref([]);
const cards = ref([]);

const total = computed(() => amounts.value.reduce((sum, item) => sum + item.amount, 0));

// 금액이 큰 순서, 비율은 소수점 한 자리
const categories = computed(() =>
  [...amounts.value]
    .sort((a, b) => b.amount - a.amount)
    .map((item) => ({ ...item, percentage: ((item.amount / total.value) * 100).toFixed(1) }))
);

const donutGradient = computed(() => {
  let start = 0;
  const stops = categories.value.map((item) => {
    const end = start + (item.amount / total.value) * 360;
    const stop = `${findCategory(item.category).color} ${start}deg ${end}deg`;
    start = end;
    return stop;
  });
  return `conic-gradient(${stops.join(', ')})`;
});

const select = async (category) => {
  selected.value = category;
  try {
    const { data } = await api.post('/posts/byCategory', { userNo, category, isOnlyPublic: !isMine.value });
    posts.value = data;
  } catch (error) {
    posts.value = [];
    console.error('카테고리별 소비 내역을 불러오지 못했습니다.', error);
  }
};

onMounted(async () => {
  try {
    const [amountRes, cardRes] = await Promise.all([
      api.get(`/users/${userNo}/amountByCategory`),
      api.get(`/users/${userNo}/recommendCards/3`),
    ]);
    amounts.value = amountRes.data;
    cards.value = cardRes.data;
    if (categories.value.length) select(categories.value[0].category);
  } catch (error) {
    console.error('소비 분석을 불러오지 못했습니다.', error);
  }
});
</script>

<style scoped>
.donut {
  width: 280px;
  height: 280px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.donut-hole {
  width: 62%;
  height: 62%;
  border-radius: 50%;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.category-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 24px;
  margin-bottom: 10px;
  border-radius: 30px;
  color: #4a4a4a;
  border: 2px solid transparent;
  transition: transform 0.1s ease;
}

.category-row:hover {
  transform: translateX(4px);
}

.category-row.selected {
  border-color: #4a4a4a;
}
</style>
