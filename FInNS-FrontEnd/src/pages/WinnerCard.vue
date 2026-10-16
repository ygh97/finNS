<template>
  <Banner title="카드 월드컵 우승" description="대망의 1위 카드와 2위~4위 카드를 소개합니다." />

  <div class="page-body">
    <!-- 새로고침하면 월드컵 결과(history state)가 사라진다 -->
    <div v-if="!winner" class="empty-message">
      <p>월드컵 결과가 없습니다.</p>
      <RouterLink to="/cardworldcup" class="btn btn-primary">다시 하기</RouterLink>
    </div>

    <template v-else>
      <section class="soft-box winner">
        <img :src="winner.imgUrl" alt="" class="winner-image" />
        <div class="flex-grow-1">
          <span class="badge badge-warning fs-6 mb-3">1위</span>
          <h2 class="fs-1 fw-bold mb-6">{{ winner.cardName }}</h2>
          <ul class="fs-4 mb-6">
            <li v-for="benefit in benefitsOf(winner)" :key="benefit" class="mb-3">{{ benefit }}</li>
          </ul>
          <RouterLink :to="`/card/${winner.cardNo}`" class="btn btn-primary">카드 보러가기</RouterLink>
          <RouterLink to="/cardworldcup" class="btn btn-light ms-2">다시 하기</RouterLink>
        </div>
      </section>

      <div class="d-flex flex-wrap justify-content-center gap-8 mt-12">
        <CardComponent v-for="card in others" :key="card.cardNo" :card="card" />
      </div>
    </template>
  </div>
</template>

<script setup>
import Banner from '@/components/common/Banner.vue';
import CardComponent from '@/components/common/CardComponent.vue';

// CardWorldCup 화면이 router state 로 넘긴 결과
const { winner, others = [] } = history.state ?? {};

const benefitsOf = (card) =>
  (card.benefit ?? '')
    .split('|')
    .map((item) => item.replace(/#/g, ' : ').trim())
    .filter(Boolean);
</script>

<style scoped>
.winner {
  display: flex;
  align-items: center;
  gap: 48px;
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px;
  border-radius: 32px;
}

.winner-image {
  width: 380px;
  max-width: 100%;
  object-fit: contain;
}

@media (max-width: 991.98px) {
  .winner {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }

  .winner ul {
    list-style: none;
    padding: 0;
  }
}
</style>
