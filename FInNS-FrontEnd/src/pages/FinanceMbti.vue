<template>
  <Banner title="금융 MBTI" description="소비 내역 분석을 바탕으로 한 나의 MBTI를 확인해 보세요." />

  <div class="page-body">
    <h2 class="section-title underline">나의 MBTI</h2>
    <section class="my-mbti">
      <img :src="myMbti.image" alt="" class="my-mbti-image" />
      <div>
        <p class="fs-1 fw-bold mb-4">{{ myMbti.name }}</p>
        <p class="fs-3 text-gray-800 mb-0">{{ myMbti.description }}</p>
      </div>
    </section>

    <h2 class="section-title underline mt-16">다른 MBTI</h2>
    <p class="text-center text-gray-600 mb-8">카드에 마우스를 올리면 설명이 보여요.</p>
    <div class="mbti-grid">
      <div v-for="mbti in MBTI_TYPES" :key="mbti.name" class="flip-card" tabindex="0">
        <div class="flip-inner">
          <div class="flip-face" :style="{ backgroundColor: mbti.color }">
            <p class="fs-2 fw-bold pt-7">{{ mbti.name }}</p>
            <img :src="mbti.image" alt="" />
          </div>
          <div class="flip-face flip-back" :style="{ backgroundColor: mbti.color }">
            <p class="fs-2 fw-bold pt-7">{{ mbti.name }}</p>
            <p class="fs-4 text-gray-800 px-6">{{ mbti.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import { findMbti, MBTI_TYPES } from '@/constants/mbti';
import Banner from '@/components/common/Banner.vue';

const auth = useAuthStore();
const mbtiName = ref(null);
const myMbti = computed(() => findMbti(mbtiName.value));

onMounted(async () => {
  try {
    const { data } = await api.get(`/users/${auth.userNo}`);
    mbtiName.value = data.mbtiName;
  } catch (error) {
    console.error('회원 정보를 불러오지 못했습니다.', error);
  }
});
</script>

<style scoped>
.underline {
  text-decoration: underline;
  text-decoration-color: skyblue;
  text-underline-offset: 8px;
  text-decoration-thickness: 2px;
}

.my-mbti {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  max-width: 900px;
  margin: 0 auto;
}

.my-mbti-image {
  width: 220px;
  flex-shrink: 0;
}

.mbti-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
  max-width: 860px;
  margin: 0 auto;
}

.flip-card {
  height: 300px;
  perspective: 1000px;
  outline: none;
}

.flip-inner {
  position: relative;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.flip-card:hover .flip-inner,
.flip-card:focus .flip-inner {
  transform: rotateY(180deg);
}

.flip-face {
  position: absolute;
  inset: 0;
  border-radius: 10px;
  text-align: center;
  backface-visibility: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.flip-face img {
  height: 190px;
  object-fit: contain;
}

.flip-back {
  transform: rotateY(180deg);
}

@media (max-width: 767.98px) {
  .my-mbti {
    flex-direction: column;
    text-align: center;
  }
}
</style>
