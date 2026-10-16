<template>
  <Banner :title="`${meta.name} 상품`" :description="`자신에게 맞는 조건을 선택하여 맞춤 ${meta.name} 상품을 확인해보세요.`" />

  <div class="page-body">
    <FinanceTabs />

    <!-- 검색·필터 -->
    <section class="card card-body shadow-sm mb-10 filters">
      <div class="mb-6">
        <label class="form-label" for="keyword">검색</label>
        <div class="position-relative search-box">
          <i class="ki-duotone ki-magnifier fs-3 text-gray-500 position-absolute top-50 translate-middle-y ms-4">
            <span class="path1"></span><span class="path2"></span>
          </i>
          <input id="keyword" v-model.trim="keyword" type="text" class="form-control form-control-solid ps-12"
            :placeholder="`은행명, ${meta.name} 상품명을 입력해주세요.`" />
        </div>
      </div>

      <div class="mb-6">
        <span class="form-label">가입 기간</span>
        <div class="d-flex flex-wrap gap-2">
          <button v-for="option in PERIODS" :key="option.label" type="button" class="btn btn-sm fw-bold"
            :class="period === option.value ? 'btn-primary' : 'btn-light'" @click="period = option.value">
            {{ option.label }}
          </button>
        </div>
      </div>

      <div class="mb-6">
        <span class="form-label">이자 계산 방식</span>
        <div class="d-flex flex-wrap gap-2">
          <button v-for="option in INTEREST_TYPES" :key="option" type="button" class="btn btn-sm fw-bold"
            :class="interestType === option ? 'btn-primary' : 'btn-light'" @click="interestType = option">
            {{ option }}
          </button>
        </div>
      </div>

      <div class="mb-6">
        <span class="form-label">가입 제한</span>
        <div class="d-flex flex-wrap gap-6">
          <label v-for="option in TARGETS" :key="option.label" class="form-check form-check-custom form-check-solid">
            <input v-model="targets" class="form-check-input" type="checkbox" :value="option.value" />
            <span class="form-check-label fw-semibold">{{ option.label }}</span>
          </label>
        </div>
      </div>

      <div>
        <span class="form-label">가입 방법</span>
        <div class="d-flex flex-wrap gap-6">
          <label v-for="way in JOIN_WAYS" :key="way" class="form-check form-check-custom form-check-solid">
            <input v-model="joinWays" class="form-check-input" type="checkbox" :value="way" />
            <span class="form-check-label fw-semibold">{{ way }}</span>
          </label>
        </div>
      </div>
    </section>

    <!-- 목록 -->
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-4 mb-4">
      <h3 class="fw-bold mb-0">{{ filtered.length }} <span class="text-gray-500 fs-6">개 상품</span></h3>
      <select v-model="sort" class="form-select form-select-solid w-auto" aria-label="정렬">
        <option value="default">기본 정렬</option>
        <option value="rate">최고 금리순</option>
      </select>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-row-bordered align-middle gy-4 gs-6">
        <thead>
          <tr class="fw-bold fs-6 text-gray-800">
            <th>은행</th>
            <th>상품명</th>
            <th>기본 금리</th>
            <th>최고 금리</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in paged" :key="item.financeProductNo" class="fw-semibold">
            <td class="text-nowrap">
              <img v-if="item.imgUrl" :src="item.imgUrl" alt="" width="28" height="24" class="me-2" />
              {{ item.korCoNm }}
            </td>
            <td>{{ item.finPrdtNm }}</td>
            <td>{{ item.intrRate }}%</td>
            <td class="text-finns fw-bold">{{ item.intrRate2 }}%</td>
            <td class="text-end">
              <RouterLink :to="`${meta.path}/${item.financeProductNo}`" class="btn btn-sm btn-primary text-nowrap">
                자세히 보기
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <p v-if="loaded && !filtered.length" class="empty-message">조건에 맞는 상품이 없습니다.</p>

    <Pagination v-model:page="page" v-model:page-size="pageSize" :total="filtered.length" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import api from '@/api';
import Banner from '@/components/common/Banner.vue';
import FinanceTabs from '@/components/common/FinanceTabs.vue';
import Pagination from '@/components/common/Pagination.vue';

// deposit: 예금(01), savings: 적금(02)
const props = defineProps({
  type: { type: String, required: true },
});

const TYPES = {
  deposit: { name: '예금', code: '01', path: '/deposit' },
  savings: { name: '적금', code: '02', path: '/installmentSavings' },
};
const PERIODS = [
  { label: '전체', value: null },
  { label: '1개월', value: 1 },
  { label: '3개월', value: 3 },
  { label: '6개월', value: 6 },
  { label: '1년', value: 12 },
  { label: '2년', value: 24 },
  { label: '3년', value: 36 },
];
const INTEREST_TYPES = ['전체', '단리', '복리'];
// joinMember - true: 가입 제한 있음(서민 전용 등), false: 누구나
const TARGETS = [
  { label: '제한 없음', value: false },
  { label: '제한 있음(서민 전용, 일부 제한)', value: true },
];
const JOIN_WAYS = ['영업점', '인터넷', '스마트폰', '모집인', '전화(텔레뱅킹)'];

const meta = computed(() => TYPES[props.type]);

const products = ref([]);
const loaded = ref(false);
const keyword = ref('');
const period = ref(null);
const interestType = ref('전체');
const targets = ref([false, true]);
const joinWays = ref([...JOIN_WAYS]);
const sort = ref('default');
const page = ref(1);
const pageSize = ref(10);

const filtered = computed(() => {
  const list = products.value.filter(
    (item) =>
      (item.finPrdtNm.includes(keyword.value) || item.korCoNm.includes(keyword.value)) &&
      (period.value === null || item.saveTrm === period.value) &&
      (interestType.value === '전체' || item.intrRateTypeNm === interestType.value) &&
      targets.value.includes(Boolean(item.joinMember)) &&
      joinWays.value.some((way) => item.joinWay?.includes(way))
  );
  return sort.value === 'rate' ? [...list].sort((a, b) => b.intrRate2 - a.intrRate2) : list;
});

const paged = computed(() => filtered.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value));

// 조건이 바뀌면 첫 페이지로
watch([keyword, period, interestType, targets, joinWays, sort], () => (page.value = 1));

onMounted(async () => {
  try {
    const { data } = await api.get(`/product/${meta.value.code}`);
    products.value = data;
  } catch (error) {
    console.error('상품 목록을 불러오지 못했습니다.', error);
  } finally {
    loaded.value = true;
  }
});
</script>

<style scoped>
.filters .form-label {
  display: block;
  font-weight: 700;
  color: #181c32;
  margin-bottom: 10px;
}

.search-box {
  max-width: 600px;
}
</style>
