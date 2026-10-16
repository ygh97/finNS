<template>
  <article v-if="post" class="card card-flush shadow-sm mb-8">
    <header class="card-header pt-7 align-items-center">
      <RouterLink :to="`/profile/${post.userNo}/spending`" class="d-flex align-items-center gap-4 text-gray-800">
        <img :src="post.userImgUrl" class="avatar" width="50" height="50" alt="" />
        <div>
          <div class="text-gray-800 text-hover-primary fs-4 fw-bold">{{ post.userName }}</div>
          <div class="text-gray-500 fw-semibold">{{ formatShortDateTime(post.transactionDate) }}</div>
        </div>
      </RouterLink>
      <RouterLink
        v-if="post.userNo === auth.userNo"
        :to="`/payDetails/${post.postNo}`"
        class="btn btn-sm btn-light btn-active-light-primary"
      >
        수정
      </RouterLink>
    </header>

    <div class="card-body pt-4">
      <ul class="list-unstyled fs-6 fw-bold text-gray-700 mb-6">
        <li class="d-flex align-items-center gap-2 mb-2">
          <i class="ki-duotone ki-geolocation fs-2 text-primary"><span class="path1"></span><span class="path2"></span></i>
          {{ post.place }}
        </li>
        <li class="d-flex align-items-center gap-2 mb-2">
          <i class="ki-duotone ki-dollar fs-2 text-warning">
            <span class="path1"></span><span class="path2"></span><span class="path3"></span>
          </i>
          {{ formatWon(post.amount) }}
        </li>
        <li class="d-flex align-items-center gap-2">
          <i class="ki-duotone ki-credit-cart fs-2 text-dark"><span class="path1"></span><span class="path2"></span></i>
          {{ post.cardName }}
        </li>
      </ul>

      <Carousel v-if="post.imgUrls?.length" class="mb-6">
        <Slide v-for="image in post.imgUrls" :key="image">
          <img :src="image" alt="" class="post-image" />
        </Slide>
        <template #addons>
          <Navigation v-if="post.imgUrls.length > 1" />
          <Pagination v-if="post.imgUrls.length > 1" />
        </template>
      </Carousel>

      <p class="fs-6 fw-bold text-gray-700">
        <span class="category-chip me-2">
          <img :src="findCategory(post.category).icon" alt="" />
          {{ post.category }}
        </span>
        에 소비했어요
      </p>
      <p v-if="post.memo" class="text-gray-600 fs-6">“{{ post.memo }}”</p>

      <div class="separator separator-solid my-3"></div>
      <div class="d-flex flex-wrap gap-2 py-1">
        <button class="btn btn-sm btn-color-gray-600 btn-active-light fw-bold" @click="showComments = !showComments">
          <i class="ki-duotone ki-message-text-2 fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
          댓글 {{ comments.length }}
        </button>
        <button class="btn btn-sm fw-bold" :class="{ 'bg-light-primary': reaction === true }" @click="react(true)">
          <i class="ki-duotone ki-like text-primary fs-2"><span class="path1"></span><span class="path2"></span></i>
          <span class="text-primary">{{ post.greatCount }} 좋은소비</span>
        </button>
        <button class="btn btn-sm fw-bold" :class="{ 'bg-light-danger': reaction === false }" @click="react(false)">
          <i class="ki-duotone ki-dislike text-danger fs-2"><span class="path1"></span><span class="path2"></span></i>
          <span class="text-danger">{{ post.stupidCount }} 이돈이면...</span>
        </button>
      </div>
      <div class="separator separator-solid my-3"></div>

      <ul v-if="showComments" class="list-unstyled">
        <li v-for="comment in comments" :key="comment.commentNo" class="d-flex gap-4 pt-4">
          <img :src="comment.imgUrl" class="avatar" width="40" height="40" alt="" />
          <div>
            <RouterLink :to="`/profile/${comment.userNo}/spending`" class="text-gray-800 text-hover-primary fw-bold">
              {{ comment.userName }}
            </RouterLink>
            <div class="text-gray-800 fs-7 pt-1">{{ comment.content }}</div>
          </div>
        </li>
      </ul>

      <form class="d-flex gap-2 mt-4" @submit.prevent="addComment">
        <input v-model.trim="newComment" type="text" class="form-control form-control-solid" placeholder="댓글을 작성하세요." />
        <button class="btn btn-primary text-nowrap" :disabled="!newComment">등록</button>
      </form>
    </div>
  </article>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import 'vue3-carousel/dist/carousel.css';
import { Carousel, Navigation, Pagination, Slide } from 'vue3-carousel';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import { findCategory } from '@/constants/categories';
import { formatShortDateTime, formatWon } from '@/util/format';

const props = defineProps({
  postNo: { type: [Number, String], required: true },
});

const auth = useAuthStore();
const post = ref(null);
const comments = ref([]);
const newComment = ref('');
const showComments = ref(true);
// true: 좋은소비, false: 이돈이면, null: 반응 없음
const reaction = ref(null);

const fetchPost = async () => {
  const { data } = await api.get(`/posts/${props.postNo}`);
  post.value = data;
};

const fetchReaction = async () => {
  const { data } = await api.get(`/greatOrStupid/${props.postNo}/isGreat`);
  reaction.value = data;
};

const fetchComments = async () => {
  const { data } = await api.get(`/comment/${props.postNo}`);
  comments.value = data;
};

const react = async (isGreat) => {
  const { data } = await api.put('/greatOrStupid', { postNo: props.postNo, greatOrStupid: isGreat });
  post.value.greatCount = data.greatCount;
  post.value.stupidCount = data.stupidCount;
  await fetchReaction();
};

const addComment = async () => {
  try {
    await api.post('/comment', { postNo: props.postNo, content: newComment.value });
    newComment.value = '';
    showComments.value = true;
    await fetchComments();
  } catch {
    alert('댓글 등록에 실패했습니다.');
  }
};

onMounted(() => {
  Promise.all([fetchPost(), fetchReaction(), fetchComments()]).catch((error) =>
    console.error('게시글을 불러오지 못했습니다.', error)
  );
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  flex-wrap: nowrap;
}

.post-image {
  width: 100%;
  height: 420px;
  object-fit: contain;
}

@media (max-width: 575.98px) {
  .post-image {
    height: 260px;
  }
}
</style>
