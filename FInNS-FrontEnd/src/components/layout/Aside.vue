<template>
  <!-- 모바일에서는 왼쪽에서 열리는 서랍(drawer)으로 동작 -->
  <div v-if="open" class="drawer-overlay d-lg-none" @click="emit('close')"></div>
  <aside
    class="aside aside-default"
    :class="{ 'drawer drawer-start': isMobile, 'drawer-on': isMobile && open }"
  >
    <div class="aside-logo px-10 pt-9 pb-5">
      <RouterLink to="/"><img alt="finNS" src="/assets/media/logos/logo-FInNS.png" width="120" /></RouterLink>
    </div>

    <nav class="aside-menu menu menu-column menu-rounded menu-title-gray-600 menu-active-bg fw-semibold fs-3 px-4">
      <div v-for="item in menus" :key="item.title" class="menu-item mt-4">
        <!-- 홈('/')은 모든 주소의 앞부분이라 정확히 같을 때만 활성 -->
        <RouterLink class="menu-link" :to="item.to" :active-class="item.to === '/' ? '' : 'active'" exact-active-class="active">
          <span class="menu-icon" :style="{ color: item.color }">
            <i :class="`ki-duotone ki-${item.icon} fs-2x`">
              <span class="path1"></span><span class="path2"></span><span class="path3"></span>
            </i>
          </span>
          <span class="menu-title ms-4">{{ item.title }}</span>
        </RouterLink>
      </div>

      <!-- 금융상품 찾기 (펼침 메뉴) -->
      <div class="menu-item menu-accordion mt-4" :class="{ 'here show': productsOpen }">
        <span class="menu-link clickable" @click="productsOpen = !productsOpen">
          <span class="menu-icon" style="color: green">
            <i class="ki-duotone ki-chart-simple fs-2x">
              <span class="path1"></span><span class="path2"></span><span class="path3"></span><span class="path4"></span>
            </i>
          </span>
          <span class="menu-title ms-4">금융상품 찾기</span>
          <span class="menu-arrow"></span>
        </span>
        <div class="menu-sub menu-sub-accordion" :style="{ display: productsOpen ? 'flex' : 'none' }">
          <div v-for="item in productMenus" :key="item.to" class="menu-item mt-1">
            <RouterLink class="menu-link" :to="item.to" active-class="active">
              <span class="menu-bullet"><span class="bullet bullet-dot"></span></span>
              <span class="menu-title ms-4">{{ item.title }}</span>
            </RouterLink>
          </div>
        </div>
      </div>

      <!-- 추천 금융상품 바로가기 -->
      <div v-for="item in recommends" :key="item.title" class="recommend-card mt-6">
        <img :src="item.icon" alt="" width="30" height="30" />
        <h4 class="fs-5 fw-bold mt-3 mb-1">{{ item.title }}</h4>
        <p class="fs-6 mb-0">
          <RouterLink :to="`/deposit/${item.deposit}`">예금 상품</RouterLink>
          <span class="text-gray-400 mx-1">|</span>
          <RouterLink :to="`/installmentSavings/${item.savings}`">적금 상품</RouterLink>
        </p>
      </div>

      <button class="btn btn-danger w-100 mt-8 mb-10" @click="logout">로그아웃</button>
    </nav>
  </aside>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';

defineProps({ open: Boolean });
const emit = defineEmits(['close']);

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const menus = computed(() => [
  { title: '홈', to: '/', icon: 'home', color: 'var(--finns-primary)' },
  { title: '프로필', to: `/profile/${auth.userNo}`, icon: 'profile-circle', color: 'palevioletred' },
  { title: '금융 MBTI', to: '/mbti', icon: 'flag', color: '#f1416c' },
  { title: '카드 월드컵', to: '/cardworldcup', icon: 'cup', color: '#ffc700' },
]);

const productMenus = [
  { title: '예금', to: '/deposit' },
  { title: '적금', to: '/installmentSavings' },
  { title: '카드', to: '/card' },
];

// 금융상품 화면에 있으면 펼쳐 둔다
const productsOpen = ref(false);
watch(
  () => route.path,
  (path) => {
    if (productMenus.some((m) => path.startsWith(m.to))) productsOpen.value = true;
  },
  { immediate: true }
);

const recommends = ref([
  { title: '금리가 가장 높은 상품', icon: '/assets/media/icons/sug1.jpg', deposit: '', savings: '' },
  { title: '최다 가입 상품', icon: '/assets/media/icons/sug2.jpg', deposit: '', savings: '' },
]);

const loadRecommends = async () => {
  const urls = ['/product/highest/deposit', '/product/highest/savings', '/product/top/deposit', '/product/top/savings'];
  try {
    const [highDeposit, highSavings, topDeposit, topSavings] = await Promise.all(urls.map((url) => api.get(url)));
    recommends.value[0].deposit = highDeposit.data.financeProductNo;
    recommends.value[0].savings = highSavings.data.financeProductNo;
    recommends.value[1].deposit = topDeposit.data.financeProductNo;
    recommends.value[1].savings = topSavings.data.financeProductNo;
  } catch (error) {
    console.error('추천 상품을 불러오지 못했습니다.', error);
  }
};

const logout = () => {
  auth.logout();
  router.push('/auth/login');
};

// 테마 CSS가 992px 미만에서 사이드바를 숨기므로 그때는 drawer로 띄운다
const mobileQuery = window.matchMedia('(max-width: 991.98px)');
const isMobile = ref(mobileQuery.matches);
const onResize = (e) => (isMobile.value = e.matches);

onMounted(() => {
  loadRecommends();
  mobileQuery.addEventListener('change', onResize);
});
onBeforeUnmount(() => mobileQuery.removeEventListener('change', onResize));
</script>

<style scoped>
.aside {
  overflow-y: auto;
  background-color: #fff;
}

.menu-icon i {
  color: inherit;
}

.aside.drawer {
  width: 280px;
}

.recommend-card {
  border: 1.5px solid #eee;
  border-radius: 12px;
  padding: 18px 20px;
  text-align: center;
}

.recommend-card h4 {
  color: #333;
}
</style>
