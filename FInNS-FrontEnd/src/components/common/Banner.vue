<template>
  <div class="banner">
    <h1 class="fw-bold text-gray-900 mb-3">{{ title }}</h1>
    <p class="fw-bold text-gray-700 fs-3 mb-0">{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
});
</script>

<style scoped>
.banner {
  background-color: var(--finns-banner);
  padding: 44px 16px 40px;
  text-align: center;
}

.banner h1 {
  font-size: 2rem;
}
</style>
