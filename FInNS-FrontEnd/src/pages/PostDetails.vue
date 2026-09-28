<template>
  <Banner
    titleText="게시물 상세 "
    descriptionText="게시물을 상세하게 확인 및 수정할 수 있습니다"
  />

  <div class="container post-edit mt-5 p-4 rounded">
    <!-- 공개 범위 선택 -->
    <div class="form-group mb-4 public-scope">
      <label for="public-range" class="form-label font-weight-bold">공개 범위</label>
      <div class="d-flex align-items-center">
        <div class="form-check me-3">
          <input
            type="radio"
            id="all"
            class="form-check-input"
            :value="true"
            v-model="post.publicStatus"
          />
          <label
            for="all"
            class="form-check-label"
            :class="{
              'selected-color': post.publicStatus === true,
              'default-color': post.publicStatus !== true,
            }"
          >
            전체공개
          </label>
        </div>
        <div class="form-check">
          <input
            type="radio"
            id="private"
            class="form-check-input"
            :value="false"
            v-model="post.publicStatus"
          />
          <label
            for="private"
            class="form-check-label"
            :class="{
              'selected-color': post.publicStatus === false,
              'default-color': post.publicStatus !== false,
            }"
          >
            비공개
          </label>
        </div>
      </div>
    </div>

    <!-- 카테고리 선택 -->
    <div class="form-group mb-4 category">
      <label for="category" class="form-label font-weight-bold">카테고리</label>
      <select id="category" class="form-select gray-input" v-model="post.category">
        <option value="">카테고리를 선택하세요.</option>
        <option value="식비 · 카페">식비 · 카페</option>
        <option value="쇼핑">쇼핑</option>
        <option value="미용">미용</option>
        <option value="의료">의료</option>
        <option value="통신">통신</option>
        <option value="교통">교통</option>
        <option value="문화 · 여행">문화 · 여행</option>
        <option value="교육">교육</option>
        <option value="술 · 유흥">술 · 유흥</option>
        <option value="기타">기타</option>
      </select>
    </div>

    <!-- 사용 금액 표시 -->
    <div class="form-group mb-4 amount">
      <label class="form-label font-weight-bold">사용 금액</label>
      <p class="amount-text">{{ post.amount.toLocaleString() }} 원</p>
    </div>

    <br />
    <br />
    <br />

    <div>
      <!--begin:: 게시물 사진-->
      <Carousel>
        <Slide v-for="(image, index) in post.imgUrls" :key="index">
          <img :src="image" alt="Image Slide" class="carousel-image" />
        </Slide>

        <template #addons>
          <Navigation />
          <Pagination />
        </template>
      </Carousel>

      <!-- 사진 추가: 서버 업로드 API가 없어 제거함 (이전 코드는 외부 데모 서버로 파일을 전송했음) -->

      <br /><br /><br />

      <!-- 저장 버튼 -->
      <div class="d-flex justify-content-between mt-4" style="margin-bottom: 100px;">
        <button class="btn btn-secondary" @click="cancel">취소</button>
        <button class="btn btn-primary" @click="confirm">확인</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import api from '@/api';
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Banner from '@/components/common/Banner.vue';
import { Carousel, Slide, Navigation, Pagination } from 'vue3-carousel';

const route = useRoute();
const router = useRouter();
const postNo = route.params.postNo;

// 기본값을 설정하여 post가 undefined일 때 오류 방지
let post = ref({
  publicStatus: '', // 공개 범위
  category: '', // 카테고리
  amount: 0, // 금액
  imgUrls: []
});

const getPost = async () => {
  try {
    const response = await api.get(`/posts/${postNo}`);
    post.value = response.data;
  } catch (error) {
    console.error('Error fetching post:', error);
  }
};

// 취소 버튼 동작
function cancel() {
  router.go(-1); // 이전 페이지로 돌아가기
}

const putPost = async () => {
  try {
    const targetData = {
      publicStatus: post.value.publicStatus,
      category: post.value.category,
      memo: post.value.memo
    };

    await api.put(`/posts/${postNo}/update`, targetData);
    return true;
  } catch (error) {
    console.error('Error updating post:', error);
    alert('저장에 실패했습니다.');
    return false;
  }
};

// 저장이 끝난 뒤에만 이동
async function confirm() {
  if (await putPost()) router.push(`/postView/${postNo}`);
}

onMounted(() => {
  getPost();
});
</script>

<style scoped>
/* 스타일 적용 */
.post-edit {
  max-width: 80%;
  margin: auto;
  background-color: #ffffff; /* 배경 색 흰색 */
}

/* 폼 요소 스타일 */
.form-label {
  font-size: 21px;
  color: #444;
  font-weight: bold;
  margin-top: 20px;
}

.amount-text {
  font-size: 17px;
  background-color: #ffffff;
  margin-left: 5px;
}

.gray-input {
  background-color: #f7f7f7;
  font-size: 17px;
  padding: 10px;
}

.selected-color {
  color: #216dbe;
  font-weight: bold;
  font-size: 17px;
}

.default-color {
  color: #000;
  font-size: 17px;
}

button {
  width: 40%;
  height: 50px;
}
p {
  color: #757575;
}

.carousel-image {
  width: 100%;
  /* 이미지가 Carousel의 너비에 맞춰짐 */
  height: 500px;
  /* contain : 사진 크기에 맞게, cover : carousel 에 맞게(이미지 잘림) */
  object-fit: contain;
}
</style>
