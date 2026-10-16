<template>
  <!-- 로그인·회원가입 공통 - 왼쪽 소개 그림, 오른쪽 입력 폼 -->
  <div class="auth-container">
    <section class="auth-brand">
      <div class="d-flex align-items-center justify-content-center gap-3 mb-6">
        <img src="/assets/media/avatars/login_logo.png" alt="" height="64" />
        <span class="brand-name">finNS</span>
      </div>
      <p class="brand-desc">카드 결제 내역이 게시글이 되는<br />소비 공유 금융 SNS</p>
      <img src="/assets/media/avatars/loginpage.png" alt="" class="brand-image" />
    </section>

    <section class="auth-form">
      <div class="auth-box">
        <RouterView />
      </div>
    </section>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  min-height: 100vh;
}

.auth-brand {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background-color: #fefefe;
  text-align: center;
}

.brand-name {
  font-size: 40px;
  font-weight: bold;
  color: #000;
}

.brand-desc {
  font-size: 18px;
  color: #5e6278;
  margin-bottom: 24px;
}

.brand-image {
  width: 100%;
  max-width: 560px;
}

.auth-form {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  background-color: var(--finns-sky);
}

.auth-box {
  width: 100%;
  max-width: 440px;
  color: #fff;
}

/* 입력 폼 공통 (로그인·회원가입 화면) */
.auth-box :deep(h1) {
  color: #fff;
  font-size: 32px;
  text-align: center;
  margin-bottom: 8px;
}

.auth-box :deep(.subtitle) {
  text-align: center;
  font-size: 17px;
  margin-bottom: 32px;
}

.auth-box :deep(.form-label) {
  color: #fff;
  font-size: 17px;
  font-weight: bold;
}

.auth-box :deep(.form-control) {
  height: 50px;
  border: none;
  border-radius: 13px;
}

.auth-box :deep(.btn-submit) {
  width: 100%;
  height: 50px;
  border-radius: 13px;
  font-size: 17px;
  background-color: #22adfc;
  color: #fff;
}

.auth-box :deep(.btn-submit:hover:not(:disabled)) {
  background-color: #0b8fdb;
}

.auth-box :deep(a) {
  color: #fff;
}

@media (max-width: 991.98px) {
  .auth-brand {
    display: none;
  }
}
</style>
