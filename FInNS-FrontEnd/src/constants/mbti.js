// 금융 MBTI(소비 성향) 9가지 - 백엔드 com.finns.Mbti 와 이름이 같아야 한다
export const MBTI_TYPES = [
  {
    name: '먹는게제일좋아형',
    image: '/assets/media/avatars/kb_eat.png',
    color: '#eff6f9',
    description: '음식에 대한 사랑은 곧 소비로 이어지는 타입! 맛집 탐방, 고급 요리, 신상 메뉴라면 지출도 아깝지 않다!!',
  },
  {
    name: '이것도저것도내꺼형',
    image: '/assets/media/avatars/kb_2my.png',
    color: '#efffec',
    description: '쇼핑몰에 가면 눈에 보이는 건 전부 내 꺼! 최신 유행 아이템부터 희귀템까지 쇼핑 카트는 언제나 가득 차 있다!',
  },
  {
    name: '자기관리마니아형',
    image: '/assets/media/avatars/kb_rabbit.png',
    color: '#fff2ff',
    description: '외모와 건강 관리를 최우선으로 생각하는 사람! 최신 뷰티 제품과 고급 서비스를 위해선 아낌없이 투자하는 유형이다!!',
  },
  {
    name: '마이아파형',
    image: '/assets/media/avatars/kb_sick.png',
    color: '#f3f2ff',
    description: '건강이 최우선! 병원비와 의료비 지출이 큰 비중을 차지하는 유형이다!! 정기적인 건강 관리와 치료를 위해 병원과 늘 가까운 사이.',
  },
  {
    name: '통신보안형',
    image: '/assets/media/avatars/kb_phone.png',
    color: '#fff6f2',
    description: '최신 휴대폰과 초고속 인터넷 없인 못 사는 유형! 데이터와 통신비 절약을 위한 소비 습관을 길러보자!!',
  },
  {
    name: '뚜벅초형',
    image: '/assets/media/avatars/kb_ddubeok.png',
    color: '#fffeed',
    description: '대중교통을 애용하는 뚜벅초형!! 지하철, 버스, 택시 등 교통비가 주된 지출이며, 경제적이고 빠른 이동을 추구하는 실속파.',
  },
  {
    name: '배낭을매고형',
    image: '/assets/media/avatars/kb_back.png',
    color: '#fff2fb',
    description: '여행을 위해 사는 당신!! 비행기 표, 숙소, 맛집 탐방까지 여행에 아낌없이 투자하며, 새로운 모험을 즐기는 여행 마니아.',
  },
  {
    name: '아인슈타인형',
    image: '/assets/media/avatars/study.png',
    color: '#ebfdff',
    description: '교육과 학습에 아낌없이 투자하는 당신!! 강의, 학원, 도서, 온라인 강좌 등 지식 습득을 위한 교육비가 가장 큰 지출 항목.',
  },
  {
    name: '술술들어간다형',
    image: '/assets/media/avatars/kb_sul.png',
    color: '#f4ebff',
    description: '술과 유흥을 즐기는 당신!! 술자리와 유흥비가 가장 큰 지출이며, 언제나 즐거운 시간을 추구하는 캐릭터.',
  },
];

// 진단 전이거나 목록에 없는 값(예: 예전에 저장된 '기타')일 때
export const MBTI_UNKNOWN = {
  name: '진단 전',
  image: '/assets/media/avatars/default_avatar.png',
  color: '#f5f8fa',
  description: '소비 내역을 바탕으로 금융 MBTI를 진단받아 보세요!',
};

export const findMbti = (name) => MBTI_TYPES.find((m) => m.name === name) ?? MBTI_UNKNOWN;
