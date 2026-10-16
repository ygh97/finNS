import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import DefaultLayout from '@/components/layout/DefaultLayout.vue';
import AuthLayout from '@/components/layout/AuthLayout.vue';

// 화면 경로를 추가하면 백엔드 SpaRoutes.PATHS 에도 추가한다 (새로고침·직접 접속 대응)
const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        { path: '', name: 'Main', component: () => import('@/pages/Main.vue') },
        { path: 'mbti', name: 'Mbti', component: () => import('@/pages/FinanceMbti.vue') },
        {
          path: 'profile/:userNo',
          name: 'Profile',
          component: () => import('@/pages/profile/Profile.vue'),
          redirect: (to) => `/profile/${to.params.userNo}/spending`,
          children: [
            { path: 'spending', component: () => import('@/pages/profile/Spending.vue') },
            { path: 'analysis', component: () => import('@/pages/profile/Analysis.vue') },
            { path: 'follower', component: () => import('@/pages/profile/FollowList.vue'), props: { type: 'follower' } },
            { path: 'following', component: () => import('@/pages/profile/FollowList.vue'), props: { type: 'following' } },
          ],
        },
        { path: 'auth/update', name: 'ProfileEdit', component: () => import('@/pages/auth/ProfilePage.vue') },
        { path: 'postView/:postNo', name: 'PostView', component: () => import('@/pages/PostViewPage.vue') },
        { path: 'payDetails/:postNo', name: 'PostEdit', component: () => import('@/pages/PostEdit.vue') },
        { path: 'deposit', name: 'Deposit', component: () => import('@/pages/FinanceProduct/ProductList.vue'), props: { type: 'deposit' } },
        { path: 'installmentSavings', name: 'Savings', component: () => import('@/pages/FinanceProduct/ProductList.vue'), props: { type: 'savings' } },
        { path: 'deposit/:financeProductNo', component: () => import('@/pages/FinanceProduct/ProductDetail.vue') },
        { path: 'installmentSavings/:financeProductNo', component: () => import('@/pages/FinanceProduct/ProductDetail.vue') },
        { path: 'card', name: 'Card', component: () => import('@/pages/FinanceProduct/Card.vue') },
        { path: 'card/:cardNo', name: 'CardDetail', component: () => import('@/pages/FinanceProduct/CardDetail.vue') },
        { path: 'cardworldcup', name: 'CardWorldCup', component: () => import('@/pages/CardWorldCup.vue') },
        { path: 'cardworldcup/winnercard', name: 'WinnerCard', component: () => import('@/pages/WinnerCard.vue') },
      ],
    },
    {
      path: '/auth',
      component: AuthLayout,
      children: [
        { path: 'login', name: 'Login', component: () => import('@/pages/auth/LoginPage.vue') },
        { path: 'join', name: 'Join', component: () => import('@/pages/auth/JoinPage.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
});

const PUBLIC_PAGES = ['Login', 'Join'];

router.beforeEach((to) => {
  const auth = useAuthStore();
  const isPublic = PUBLIC_PAGES.includes(to.name);
  if (!isPublic && !auth.isLogin) return { name: 'Login' };
  if (isPublic && auth.isLogin) return { name: 'Main' };
});

export default router;
