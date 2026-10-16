<template>
  <div class="page-body">
    <SearchUser class="mb-8" />

    <div class="row g-8">
      <!-- 피드: 사진이 있는 공개 소비글, 스크롤하면 더 불러옴 -->
      <div class="col-xl-8">
        <PostView v-for="postNo in visiblePostNos" :key="postNo" :post-no="postNo" />
        <p v-if="!loaded" class="empty-message">게시글을 불러오는 중입니다...</p>
        <p v-else-if="!allPostNos.length" class="empty-message">아직 게시글이 없습니다.</p>
        <div ref="sentinel"></div>
      </div>

      <aside class="col-xl-4">
        <div class="side-sticky">
          <!-- 이달의 랭킹 -->
          <section class="card shadow-sm mb-8">
            <div class="card-body">
              <h2 class="fs-2 fw-bold text-center mb-8">이달의 TOP 랭킹</h2>

              <h3 class="ranking-title">
                <i class="ki-duotone ki-cup fs-1 text-warning"><span class="path1"></span><span class="path2"></span></i>
                소비 TOP 3
              </h3>
              <p v-if="!topUsers.length" class="text-gray-500 ms-2">이번 달 소비 내역이 없습니다.</p>
              <ol class="ranking-list">
                <li v-for="(user, idx) in topUsers" :key="user.userNo">
                  <span class="rank">{{ idx + 1 }}</span>
                  <img :src="user.imgUrl" class="avatar" width="36" height="36" alt="" />
                  <RouterLink :to="`/profile/${user.userNo}/spending`" class="flex-grow-1 text-gray-800 text-hover-primary fw-bold">
                    {{ user.userName }}
                  </RouterLink>
                  <span class="fw-semibold">{{ formatWon(user.totalAmount) }}</span>
                </li>
              </ol>

              <h3 class="ranking-title mt-6">
                <i class="ki-duotone ki-like fs-1 text-primary"><span class="path1"></span><span class="path2"></span></i>
                좋은소비 TOP 3
              </h3>
              <ol class="ranking-list">
                <li v-for="(post, idx) in topPosts" :key="post.postNo">
                  <span class="rank">{{ idx + 1 }}</span>
                  <img :src="findCategory(post.category).icon" width="36" height="36" alt="" />
                  <RouterLink :to="`/postView/${post.postNo}`" class="flex-grow-1 text-gray-800 text-hover-primary fw-bold">
                    {{ post.place }}
                  </RouterLink>
                  <span class="fw-semibold">{{ formatWon(post.amount) }}</span>
                </li>
              </ol>
            </div>
          </section>

          <!-- 같은 소비 성향 회원 추천 -->
          <section class="card shadow-sm">
            <div class="card-body">
              <h2 class="fs-2 fw-bold mb-1">회원님을 위한 추천</h2>
              <p class="text-muted fw-bold mb-6">나와 비슷한 소비 습관을 가진 사용자</p>
              <p v-if="!recommendUsers.length" class="text-gray-500">MBTI를 진단하면 추천을 받을 수 있어요.</p>
              <div v-for="user in recommendUsers" :key="user.userNo" class="d-flex align-items-center gap-4 py-3">
                <img :src="user.imgUrl" class="avatar" width="40" height="40" alt="" />
                <RouterLink :to="`/profile/${user.userNo}/spending`" class="flex-grow-1 text-gray-800 text-hover-primary fs-6 fw-bold">
                  {{ user.userName }}
                </RouterLink>
                <FollowButton :user-no="user.userNo" :initial-following="user.follow" />
              </div>
            </div>
          </section>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import api from '@/api';
import { useAuthStore } from '@/stores/auth';
import { findCategory } from '@/constants/categories';
import { formatWon } from '@/util/format';
import FollowButton from '@/components/common/FollowButton.vue';
import PostView from '@/components/common/PostView.vue';
import SearchUser from '@/components/common/SearchUser.vue';

const PAGE_SIZE = 3;

const auth = useAuthStore();
const allPostNos = ref([]);
const visiblePostNos = ref([]);
const loaded = ref(false);
const topUsers = ref([]);
const topPosts = ref([]);
const recommendUsers = ref([]);

// 목록 끝(sentinel)이 화면에 보이면 다음 게시글을 붙인다
const sentinel = ref(null);
let observer;
const showMore = () => {
  const next = allPostNos.value.slice(visiblePostNos.value.length, visiblePostNos.value.length + PAGE_SIZE);
  visiblePostNos.value.push(...next);
};

const loadFeed = async () => {
  const { data } = await api.get('/posts/images/distinct');
  allPostNos.value = data;
  loaded.value = true;
  showMore();
  observer = new IntersectionObserver((entries) => entries[0].isIntersecting && showMore(), { rootMargin: '400px' });
  observer.observe(sentinel.value);
};

const loadSidebar = async () => {
  const now = new Date();
  const [usersRes, postsRes, recommendRes] = await Promise.all([
    api.get(`/users/top3/${now.getFullYear()}/${now.getMonth() + 1}`),
    api.get('/posts/top3'),
    api.get(`/users/${auth.userNo}/recommend5`),
  ]);
  topUsers.value = usersRes.data;
  topPosts.value = postsRes.data;
  recommendUsers.value = recommendRes.data;
};

onMounted(() => {
  loadFeed().catch((error) => console.error('피드를 불러오지 못했습니다.', error));
  loadSidebar().catch((error) => console.error('랭킹을 불러오지 못했습니다.', error));
});
onBeforeUnmount(() => observer?.disconnect());
</script>

<style scoped>
.side-sticky {
  position: sticky;
  top: 24px;
}

.ranking-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.ranking-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.ranking-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
}

.rank {
  width: 24px;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
</style>
