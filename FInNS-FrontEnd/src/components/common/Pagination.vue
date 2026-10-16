<template>
  <div class="d-flex flex-wrap justify-content-between align-items-center gap-4 mt-6">
    <select
      :value="pageSize"
      class="form-select form-select-sm form-select-solid w-auto"
      aria-label="한 페이지에 보일 개수"
      @change="emit('update:pageSize', Number($event.target.value)); emit('update:page', 1)"
    >
      <option v-for="size in pageSizes" :key="size" :value="size">{{ size }}개씩</option>
    </select>

    <ul class="pagination mb-0">
      <li class="page-item previous" :class="{ disabled: page <= 1 }">
        <a href="#" class="page-link" @click.prevent="go(page - 1)">이전</a>
      </li>
      <li v-for="p in visiblePages" :key="p" class="page-item" :class="{ active: p === page }">
        <a href="#" class="page-link" @click.prevent="go(p)">{{ p }}</a>
      </li>
      <li class="page-item next" :class="{ disabled: page >= totalPages }">
        <a href="#" class="page-link" @click.prevent="go(page + 1)">다음</a>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  page: { type: Number, required: true },
  pageSize: { type: Number, required: true },
  total: { type: Number, required: true },
  pageSizes: { type: Array, default: () => [10, 25, 50] },
});
const emit = defineEmits(['update:page', 'update:pageSize']);

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)));

// 현재 페이지 앞뒤로 2페이지씩
const visiblePages = computed(() => {
  const start = Math.max(1, props.page - 2);
  const end = Math.min(totalPages.value, props.page + 2);
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

const go = (p) => {
  if (p >= 1 && p <= totalPages.value) emit('update:page', p);
};
</script>
