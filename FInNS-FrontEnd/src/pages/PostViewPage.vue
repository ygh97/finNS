<template>
  <div class="page-body narrow">
    <PostView :post-no="route.params.postNo" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
import PostView from '@/components/common/PostView.vue';

const route = useRoute();
</script>
