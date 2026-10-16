<template>
  <Banner title="카드 월드컵" description="더 마음에 드는 카드를 골라주세요." />

  <div class="page-body">
    <p v-if="!loading && cards.length < 2" class="empty-message">
      소비 내역이 있어야 추천 카드로 월드컵을 열 수 있어요.
    </p>

    <template v-else-if="match.length === 2">
      <h2 class="text-center fs-1 fw-bold my-8">
        {{ roundLabel }}
        <span v-if="roundSize > 2" class="text-gray-500 fs-3 ms-2">{{ matchIndex + 1 }} / {{ roundSize / 2 }}</span>
      </h2>
      <div class="match">
        <template v-for="(card, idx) in match" :key="card.cardNo">
          <div class="soft-box candidate">
            <h3 class="fs-3 fw-bold mb-6">{{ card.cardName }}</h3>
            <img :src="card.imgUrl" alt="" class="card-image" />
            <ul class="list-unstyled fs-5 mt-6">
              <li v-for="benefit in benefitsOf(card)" :key="benefit">{{ benefit }}</li>
            </ul>
            <button class="btn btn-primary px-10" @click="pick(card)">선택</button>
          </div>
          <span v-if="idx === 0" class="versus">VS</span>
        </template>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import Banner from '@/components/common/Banner.vue';

const ROUND_START = 8;

const router = useRouter();
const auth = useAuthStore();

const loading = ref(true);
const cards = ref([]);
// 이번 라운드에 남은 카드, 이번 라운드에서 이긴 카드
const remaining = ref([]);
const winners = ref([]);
const semifinalists = ref([]);
const roundSize = ref(ROUND_START);
const matchIndex = ref(0);

const match = computed(() => remaining.value.slice(0, 2));
const roundLabel = computed(() => (roundSize.value === 2 ? '결승전' : `${roundSize.value}강`));

const benefitsOf = (card) =>
  (card.benefit ?? '')
    .split('|')
    .map((item) => item.replace(/#/g, ' ').trim())
    .filter(Boolean);

const pick = (card) => {
  winners.value.push(card);
  remaining.value = remaining.value.slice(2);
  matchIndex.value++;
  if (remaining.value.length) return;

  // 라운드 종료 - 우승이면 결과 화면으로, 아니면 이긴 카드로 다음 라운드
  if (winners.value.length === 1) {
    const others = semifinalists.value.filter((c) => c.cardNo !== card.cardNo);
    router.push({ name: 'WinnerCard', state: { winner: { ...card }, others: others.map((c) => ({ ...c })) } });
    return;
  }
  startRound(winners.value);
};

const startRound = (roundCards) => {
  remaining.value = roundCards;
  winners.value = [];
  roundSize.value = roundCards.length;
  matchIndex.value = 0;
  // 우승 화면에서 2~4위로 보여줄 4강 카드
  if (roundCards.length === 4 || !semifinalists.value.length) semifinalists.value = [...roundCards];
};

onMounted(async () => {
  try {
    const { data } = await api.get(`/users/${auth.userNo}/recommendCards/${ROUND_START}`);
    cards.value = data;
    // 카드가 8장보다 적으면 그 수에 맞는 가장 큰 2의 거듭제곱으로 시작
    let size = ROUND_START;
    while (size > data.length) size /= 2;
    if (size >= 2) startRound(data.slice(0, size));
  } catch (error) {
    console.error('추천 카드를 불러오지 못했습니다.', error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.match {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
}

.candidate {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  width: 340px;
}

.card-image {
  width: 100%;
  max-width: 280px;
  height: 170px;
  object-fit: contain;
}

.versus {
  font-size: 2rem;
  font-weight: 800;
  color: var(--finns-primary);
}

@media (max-width: 767.98px) {
  .match {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
