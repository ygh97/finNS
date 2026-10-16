<template>
  <div v-if="product" class="page-body narrow">
    <div class="d-flex align-items-center gap-3 mb-3">
      <img v-if="product.imgUrl" :src="product.imgUrl" alt="" width="44" height="44" />
      <span class="fs-3 fw-bolder text-gray-600">{{ product.korCoNm }}</span>
      <span class="badge badge-light-primary fs-7">{{ product.financeProductType === '01' ? '예금' : '적금' }}</span>
    </div>
    <h1 class="fs-2hx fw-bold mb-8">{{ product.finPrdtNm }}</h1>

    <div class="rate-box mb-10">
      <div>
        <div class="text-gray-600 fs-6">최고 금리</div>
        <div class="fs-2hx fw-bold text-finns">{{ product.intrRate2 }}%</div>
      </div>
      <div>
        <div class="text-gray-600 fs-6">기본 금리</div>
        <div class="fs-2x fw-bold">{{ product.intrRate }}%</div>
      </div>
    </div>

    <dl class="detail-list">
      <dt>가입 대상</dt>
      <dd>{{ product.joinMember ? '제한 있음 (서민 전용, 일부 제한)' : '누구나' }}</dd>
      <dt>가입 방법</dt>
      <dd>{{ product.joinWay?.replaceAll(',', ', ') }}</dd>
      <dt>가입 기간</dt>
      <dd>{{ product.saveTrm }}개월</dd>
      <dt>이자 계산</dt>
      <dd>{{ product.intrRateTypeNm }}</dd>
      <dt>우대 조건</dt>
      <dd class="text-finns">{{ product.spclCnd }}</dd>
      <dt>만기 후 이자율</dt>
      <dd>
        <div v-for="line in maturityRates" :key="line">{{ line }}</div>
      </dd>
    </dl>

    <a v-if="product.companyUrl" :href="product.companyUrl" target="_blank" rel="noopener" class="btn btn-primary mt-10">
      은행 홈페이지에서 자세히 보기
    </a>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/api';

const route = useRoute();
const product = ref(null);

// 'A | B' 형식
const maturityRates = computed(() => (product.value?.mtrtInt ?? '').split('|').map((s) => s.trim()).filter(Boolean));

onMounted(async () => {
  try {
    const { data } = await api.get(`/product/no/${route.params.financeProductNo}`);
    product.value = data;
  } catch (error) {
    console.error('상품 정보를 불러오지 못했습니다.', error);
  }
});
</script>

<style scoped>
.rate-box {
  display: flex;
  gap: 48px;
  padding: 24px 28px;
  border-radius: 12px;
  background-color: var(--finns-soft);
}

.detail-list {
  display: grid;
  grid-template-columns: 140px 1fr;
  row-gap: 18px;
  font-size: 1.1rem;
}

.detail-list dt {
  color: #5e6278;
}

.detail-list dd {
  margin: 0;
  font-weight: 600;
}

@media (max-width: 575.98px) {
  .detail-list {
    grid-template-columns: 1fr;
    row-gap: 4px;
  }

  .detail-list dd {
    margin-bottom: 12px;
  }
}
</style>
