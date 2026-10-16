<template>
  <RouterLink :to="`/card/${card.cardNo}`" class="soft-box card-item">
    <h3 class="fs-3 fw-bold mb-6 text-gray-900">{{ card.cardName }}</h3>
    <img :src="card.imgUrl" class="card-image" alt="" />
    <ul class="list-unstyled fs-6 text-gray-700 mt-6 mb-4">
      <li v-for="benefit in benefits" :key="benefit">{{ benefit }}</li>
    </ul>
    <span class="fs-7 text-gray-500">자세한 정보 알아보기 ›</span>
  </RouterLink>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  card: { type: Object, required: true },
});

// 요약 혜택: '혜택#설명 | 혜택#설명'
const benefits = computed(() =>
  (props.card.benefit ?? '')
    .split('|')
    .map((item) => item.replace(/#/g, ' : ').trim())
    .filter(Boolean)
);
</script>

<style scoped>
.card-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  width: 100%;
  max-width: 320px;
  transition: transform 0.15s ease;
}

.card-item:hover {
  transform: translateY(-4px);
}

.card-image {
  width: 100%;
  max-width: 260px;
  height: 150px;
  object-fit: contain;
}
</style>
