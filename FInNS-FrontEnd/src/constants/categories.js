// 소비 카테고리 - DB post.category 값과 같아야 한다
export const CATEGORIES = [
  { name: '식비 · 카페', icon: '/assets/media/category/meal.png', color: '#8DC3FF' },
  { name: '쇼핑', icon: '/assets/media/category/shopping.png', color: '#FF8690' },
  { name: '미용', icon: '/assets/media/category/beauty.png', color: '#D46B7E' },
  { name: '의료', icon: '/assets/media/category/health.png', color: '#88E793' },
  { name: '통신', icon: '/assets/media/category/communication.png', color: '#EEA4FB' },
  { name: '교통', icon: '/assets/media/category/car.png', color: '#7ED7E9' },
  { name: '문화 · 여행', icon: '/assets/media/category/travel.png', color: '#5ECBCB' },
  { name: '교육', icon: '/assets/media/category/study.png', color: '#B5BDC4' },
  { name: '술 · 유흥', icon: '/assets/media/category/play.png', color: '#FFBE5F' },
  { name: '기타', icon: '/assets/media/category/else.png', color: '#DCE775' },
];

const OTHER = CATEGORIES[CATEGORIES.length - 1];

export const findCategory = (name) => CATEGORIES.find((c) => c.name === name) ?? OTHER;
