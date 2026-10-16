<template>
  <div class="page-body">
    <SearchUser class="mb-6" />

    <!-- 프로필 요약 -->
    <section class="card shadow-sm mb-8">
      <div class="card-body d-flex flex-wrap flex-sm-nowrap gap-7">
        <img :src="user.imgUrl" class="profile-image" alt="" />

        <div class="flex-grow-1">
          <div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-2">
            <div>
              <div class="d-flex align-items-center gap-4 mb-2">
                <span class="text-gray-900 fs-2 fw-bold">{{ user.userName }}</span>
                <FollowButton v-if="!isMine" :user-no="userNo" :initial-following="following" />
              </div>
              <RouterLink to="/mbti" class="d-inline-flex align-items-center gap-1 text-gray-500 text-hover-primary fs-6">
                <i class="ki-duotone ki-profile-circle fs-4"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                {{ user.mbtiName || 'MBTI 정보 없음' }}
              </RouterLink>
            </div>

            <div v-if="isMine" class="d-flex gap-2">
              <button class="btn btn-sm btn-primary" @click="diagnoseMbti">금융 MBTI 진단</button>
              <RouterLink to="/auth/update" class="btn btn-sm btn-light">프로필 수정</RouterLink>
            </div>
          </div>

          <div class="d-flex flex-wrap gap-4 mt-4">
            <RouterLink v-for="stat in stats" :key="stat.label" :to="stat.to" class="stat-box">
              <div class="fs-6 fw-bold text-gray-900">{{ stat.label }}</div>
              <CountUp class="fs-2 fw-bold text-primary" :end-val="stat.value" />
            </RouterLink>
          </div>

          <div class="d-flex justify-content-end align-items-center gap-2 mt-4 text-gray-600">
            <span>마지막 갱신 {{ user.renewTime }}</span>
            <button v-if="isMine" class="btn btn-icon btn-sm btn-light-primary" title="소비 내역 갱신" @click="renew">
              <i class="ki-duotone ki-arrows-circle fs-2"><span class="path1"></span><span class="path2"></span></i>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 가입한 금융상품 -->
    <h2 class="section-title">가입한 금융상품</h2>
    <div class="row g-8 align-items-center">
      <div class="col-lg-6">
        <p v-if="!products.length" class="empty-message">가입한 예·적금이 없습니다.</p>
        <RouterLink
          v-for="product in products"
          :key="product.financeProductNo"
          :to="`/deposit/${product.financeProductNo}`"
          class="product-row"
        >
          <img v-if="product.imgUrl" :src="product.imgUrl" alt="" width="28" height="24" />
          <span class="text-gray-900 fw-bold">{{ product.korCoNm }}</span>
          <span class="text-gray-600 fw-bold text-hover-primary">{{ product.finPrdtNm }}</span>
        </RouterLink>
      </div>
      <div class="col-lg-6">
        <Carousel v-if="cards.length">
          <Slide v-for="card in cards" :key="card.cardNo">
            <RouterLink :to="`/card/${card.cardNo}`" class="text-center">
              <img :src="card.imgUrl" class="card-image" alt="" />
              <div class="mt-3 fs-3 fw-bold text-gray-900 text-hover-primary">{{ card.cardName }}</div>
            </RouterLink>
          </Slide>
          <template #addons>
            <Navigation />
            <Pagination />
          </template>
        </Carousel>
      </div>
    </div>

    <!-- 소비 내역 / 분석 / 팔로워 / 팔로잉 -->
    <ul class="nav nav-line-tabs nav-line-tabs-2x border-transparent fs-4 fw-bold justify-content-center gap-8 mt-12 mb-8">
      <li v-for="tab in tabs" :key="tab.to" class="nav-item">
        <RouterLink class="nav-link text-active-primary py-4" :to="tab.to" active-class="active">{{ tab.label }}</RouterLink>
      </li>
    </ul>
    <RouterView />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import CountUp from 'vue-countup-v3';
import 'vue3-carousel/dist/carousel.css';
import { Carousel, Navigation, Pagination, Slide } from 'vue3-carousel';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import FollowButton from '@/components/common/FollowButton.vue';
import SearchUser from '@/components/common/SearchUser.vue';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const userNo = Number(route.params.userNo);
const isMine = computed(() => userNo === auth.userNo);
const base = `/profile/${userNo}`;

const user = ref({});
const following = ref(false);
const spendingCount = ref(0);
const followerCount = ref(0);
const followingCount = ref(0);
const products = ref([]);
const cards = ref([]);

const stats = computed(() => [
  { label: '소비 내역', value: spendingCount.value, to: `${base}/spending` },
  { label: '팔로워', value: followerCount.value, to: `${base}/follower` },
  { label: '팔로잉', value: followingCount.value, to: `${base}/following` },
]);

const tabs = [
  { label: '소비 내역', to: `${base}/spending` },
  { label: '분석', to: `${base}/analysis` },
  { label: '팔로워', to: `${base}/follower` },
  { label: '팔로잉', to: `${base}/following` },
];

const load = async () => {
  const [userRes, followRes, countRes, followCountRes, productRes, cardRes] = await Promise.all([
    api.get(`/users/${userNo}`),
    api.get('/followingcheck', { params: { to_user_no: userNo } }),
    api.get(`/users/${userNo}/posts/count`),
    api.get(`/users/${userNo}/followCounts`),
    api.get(`/product/users/${userNo}/products`),
    api.get(`/users/${userNo}/cards`),
  ]);
  user.value = userRes.data;
  following.value = followRes.data;
  spendingCount.value = countRes.data;
  followerCount.value = followCountRes.data.followerCount;
  followingCount.value = followCountRes.data.followingCount;
  products.value = productRes.data;
  cards.value = cardRes.data;
};

// 마지막 갱신 이후의 결제 내역을 반영
const renew = async () => {
  await api.put(`/users/${userNo}/renew`);
  location.reload();
};

const diagnoseMbti = async () => {
  await api.put(`/users/${userNo}/mbti`).catch((error) => console.error('MBTI 진단 실패', error));
  router.push('/mbti');
};

onMounted(() => load().catch((error) => console.error('프로필을 불러오지 못했습니다.', error)));
</script>

<style scoped>
.profile-image {
  width: 140px;
  height: 140px;
  border-radius: 12px;
  object-fit: cover;
}

.stat-box {
  min-width: 120px;
  padding: 12px 16px;
  border: 1px dashed #e1e3ea;
  border-radius: 8px;
  text-align: center;
}

.product-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 8px;
  font-size: 1.1rem;
  border-bottom: 1px solid #f1f1f4;
}

.card-image {
  width: 260px;
  max-width: 100%;
  height: 150px;
  object-fit: contain;
}

@media (max-width: 575.98px) {
  .profile-image {
    width: 96px;
    height: 96px;
  }
}
</style>
