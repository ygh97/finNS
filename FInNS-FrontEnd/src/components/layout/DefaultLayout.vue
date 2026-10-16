<template>
  <div class="d-flex flex-column flex-root">
    <!-- 모바일 상단 바 (데스크톱에서는 사이드바가 항상 보임) -->
    <header class="mobile-header d-flex d-lg-none">
      <RouterLink to="/"><img src="/assets/media/logos/logo-FInNS.png" alt="finNS" height="30" /></RouterLink>
      <button class="btn btn-icon btn-active-light-primary" aria-label="메뉴 열기" @click="asideOpen = true">
        <i class="ki-duotone ki-abstract-14 fs-1"><span class="path1"></span><span class="path2"></span></i>
      </button>
    </header>

    <div class="page d-flex flex-row flex-column-fluid p-0">
      <Aside :open="asideOpen" @close="asideOpen = false" />
      <div class="wrapper d-flex flex-column flex-row-fluid">
        <!-- 같은 화면에서 주소의 번호만 바뀌어도(다른 회원 프로필 등) 새로 그린다 -->
        <RouterView :key="JSON.stringify(route.params)" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Aside from '@/components/layout/Aside.vue';

const route = useRoute();
const asideOpen = ref(false);

watch(() => route.fullPath, () => (asideOpen.value = false));
</script>

<style scoped>
.mobile-header {
  position: sticky;
  top: 0;
  z-index: 100;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  padding: 0 16px;
  background-color: #fff;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
}
</style>
