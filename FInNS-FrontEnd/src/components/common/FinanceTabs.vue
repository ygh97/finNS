<template>
  <ul class="nav nav-tabs nav-line-tabs nav-line-tabs-2x fs-3 fw-bold mb-8">
    <li v-for="tab in tabs" :key="tab.to" class="nav-item">
      <RouterLink class="nav-link text-active-primary py-4 me-6" :to="tab.to" exact-active-class="active">
        {{ tab.label }}
      </RouterLink>
    </li>
  </ul>
</template>

<script setup>
const tabs = [
  { label: '예금', to: '/deposit' },
  { label: '적금', to: '/installmentSavings' },
  { label: '카드', to: '/card' },
];
</script>
