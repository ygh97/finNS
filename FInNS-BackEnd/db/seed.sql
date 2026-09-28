-- =====================================================================
-- finNS 예시 데이터 (schema.sql 적용 직후 실행)
--   mariadb -u root --default-character-set=utf8mb4 < db/seed.sql
--
-- - 회원 12명, 비밀번호는 모두 1234  (로그인 예: demo / 1234)
-- - 카드 100장: 이미지(assets/card/N.png)만 실제 파일이고 이름·혜택은 예시 값
-- - 소비 내역: 2026-07-01 ~ 2026-10-31 카드 거래. 실행 시각까지는 반영(renew) 완료,
--   이후 거래는 시간이 지나면 앱의 갱신 기능이 반영한다.
-- - 무작위 대신 CRC32 해시를 써서 몇 번을 실행해도 같은 데이터가 나온다.
-- =====================================================================

USE finns;
SET NAMES utf8mb4;

SET @now = NOW();
SET @pw = '$2a$10$LXP.t5mm2QGfWTJv1UyoSuXZPwCL7f3qrtuEraNYL44gotiIO4VSW'; -- BCrypt('1234')

-- ---------------------------------------------------------------------
-- 작업용 테이블 (마지막에 삭제)
-- ---------------------------------------------------------------------
CREATE TABLE seed_seq (n INT PRIMARY KEY);
INSERT INTO seed_seq (n)
WITH d AS (SELECT 0 AS v UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
           UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9)
SELECT a.v + 10 * b.v + 100 * c.v + 1000 * e.v FROM d a, d b, d c, d e;

-- 카드 혜택 분류 (CardService.matchingCategory, Card.vue 탭과 같은 6개)
CREATE TABLE seed_card_type (idx INT PRIMARY KEY, name VARCHAR(20), card_nick VARCHAR(20), benefit VARCHAR(50));
INSERT INTO seed_card_type VALUES
(1, '식비', '딜리셔스', '음식점·카페·배달앱'),
(2, '여가', '플레이',   '쇼핑·뷰티·영화·여행'),
(3, '교통', '무브',     '대중교통·택시'),
(4, '의료', '헬스케어', '병원·약국'),
(5, '통신', '올인원',   '통신요금 자동이체'),
(6, '교육', '스터디',   '학원·도서·온라인강의');

-- 소비 카테고리별 MBTI(com.finns.Mbti), 거래 금액 범위, 가맹점, 대응 카드 분류
CREATE TABLE seed_category (
    idx            INT PRIMARY KEY,
    name           VARCHAR(20),
    mbti           VARCHAR(20),
    point          INT,
    lo             INT,
    hi             INT,
    places         VARCHAR(255),
    card_type_idx  INT
);
INSERT INTO seed_category VALUES
(1,  '식비 · 카페', '먹는게제일좋아형',   100,  4000,  45000,  '스타벅스|메가커피|김밥천국|교촌치킨|배달의민족',                 1),
(2,  '쇼핑',        '이것도저것도내꺼형', 70,   15000, 180000, '무신사|쿠팡|29CM|다이소|이마트',                                2),
(3,  '미용',        '자기관리마니아형',   50,   10000, 120000, '올리브영|준오헤어|아이디헤어|네일아트|이니스프리',              2),
(4,  '의료',        '마이아파형',         60,   5000,  80000,  '연세내과|서울치과|온누리약국|튼튼정형외과|밝은안과',            4),
(5,  '통신',        '통신보안형',         60,   30000, 90000,  'SKT 통신요금|KT 통신요금|LG U+ 통신요금|알뜰폰 요금|인터넷 요금', 5),
(6,  '교통',        '뚜벅초형',           20,   1400,  35000,  '지하철|시내버스|카카오T 택시|코레일|티머니 충전',               3),
(7,  '문화 · 여행', '배낭을매고형',       100,  12000, 250000, 'CGV|메가박스|인터파크 티켓|야놀자|대한항공',                    2),
(8,  '교육',        '아인슈타인형',       50,   15000, 200000, '교보문고|인프런|영어학원|클래스101|YES24',                      6),
(9,  '술 · 유흥',   '술술들어간다형',     100,  15000, 120000, '이자카야|포장마차|노래방|와인바|수제맥주집',                    1),
(10, '기타',        NULL,                 NULL, 3000,  50000,  'GS25|CU|세븐일레븐|세탁소|우체국',                              NULL);

-- 회원별 주 소비 카테고리: 회원 u 와 u+6 이 같은 성향이 되도록 6개를 돌려 씀
CREATE TABLE seed_user_fav (user_no BIGINT PRIMARY KEY, cat_idx INT);
INSERT INTO seed_user_fav
SELECT n, ELT((n - 1) % 6 + 1, 1, 2, 7, 9, 3, 8) FROM seed_seq WHERE n BETWEEN 1 AND 12;

CREATE TABLE seed_corp (idx INT PRIMARY KEY, name VARCHAR(50), short VARCHAR(20), url VARCHAR(255));
INSERT INTO seed_corp VALUES
(1,  'KB국민카드', 'KB국민', 'https://card.kbcard.com'),
(2,  '신한카드',   '신한',   'https://www.shinhancard.com'),
(3,  '삼성카드',   '삼성',   'https://www.samsungcard.com'),
(4,  '현대카드',   '현대',   'https://www.hyundaicard.com'),
(5,  '롯데카드',   '롯데',   'https://www.lottecard.co.kr'),
(6,  '우리카드',   '우리',   'https://pc.wooricard.com'),
(7,  '하나카드',   '하나',   'https://www.hanacard.co.kr'),
(8,  'NH농협카드', 'NH',     'https://card.nonghyup.com'),
(9,  'BC카드',     'BC',     'https://www.bccard.com'),
(10, 'IBK기업은행', 'IBK',   'https://www.ibk.co.kr');

-- 게시글 사진: assets/peed/{post_no}/{파일}
CREATE TABLE seed_peed (post_no BIGINT, file VARCHAR(20));
INSERT INTO seed_peed VALUES
(43,'1.jpg'),(43,'2.jpg'),(43,'3.jpg'),(43,'4.jpg'),(43,'5.jpg'),
(76,'1.jpg'),
(85,'1.jpg'),(85,'2.jpg'),(85,'3.jpg'),(85,'4.jpg'),(85,'5.jpg'),(85,'6.jpg'),
(158,'1.jpg'),(158,'2.jpg'),(158,'3.jpg'),
(227,'1.jpg'),(227,'2.jpg'),(227,'3.jpg'),(227,'4.jpg'),(227,'5.png'),(227,'6.png'),(227,'7.jpg'),(227,'8.png'),
(278,'1.jpg'),(278,'2.jpg'),(278,'3.jpg'),(278,'4.jpg'),(278,'5.jpg'),
(345,'1.jpg'),(345,'2.jpg'),(345,'3.jpg'),(345,'4.jpg'),
(352,'1.jpg'),(352,'2.jpg'),(352,'3.jpg'),(352,'4.jpg'),(352,'5.jpg'),
(380,'1.jpg'),(380,'2.jpg'),
(383,'1.jpg'),(383,'2.jpg'),(383,'3.jpg'),(383,'4.jpg'),
(452,'1.jpg'),(452,'2.jpg'),(452,'3.jpg'),(452,'4.jpg'),
(518,'1.jpg'),
(557,'1.jpg'),(557,'2.jpg'),(557,'3.jpg'),(557,'4.jpg'),(557,'5.jpg'),
(558,'1.jpg'),(558,'2.jpg'),(558,'3.jpg'),(558,'4.jpg'),
(623,'1.png'),
(779,'1.png'),
(920,'1.jpg'),(920,'2.jpg'),(920,'3.jpg'),(920,'4.jpg'),(920,'5.jpg'),(920,'6.jpg'),
(923,'1.jpg'),
(960,'1.jpg'),(960,'2.jpg'),(960,'3.jpg'),(960,'4.jpg'),
(988,'1.jpg'),(988,'2.jpg'),(988,'3.jpg');

-- ---------------------------------------------------------------------
-- 회원
-- ---------------------------------------------------------------------
INSERT INTO user (user_no, username, password, birth, img_url) VALUES
(1,  'demo',    @pw, '1999-03-14', '/assets/profile/1.jpg'),
(2,  'minsu',   @pw, '1997-07-02', '/assets/profile/2.jpg'),
(3,  'jiwoo',   @pw, '2000-11-21', '/assets/profile/3.jpg'),
(4,  'seoyeon', @pw, '1998-01-09', '/assets/profile/4.png'),
(5,  'haneul',  @pw, '1995-05-30', '/assets/profile/5.jpg'),
(6,  'doyun',   @pw, '2001-08-17', '/assets/profile/6.jpg'),
(7,  'yuna',    @pw, '1996-12-03', '/assets/profile/7.jpg'),
(8,  'jihoon',  @pw, '1999-09-25', '/assets/profile/8.jpg'),
(9,  'soyeon',  @pw, '2002-02-11', '/assets/profile/9.jpeg'),
(10, 'taeyang', @pw, '1994-04-06', '/assets/profile/10.jpg'),
(11, 'eunji',   @pw, '1998-10-19', '/assets/profile/11.webp'),
(12, 'junho',   @pw, '2000-06-28', '/assets/profile/12.webp');

INSERT INTO user_auth (username, authority)
SELECT username, 'ROLE_MEMBER' FROM user;

-- 회원마다 3~5명 정도 팔로우
INSERT INTO follow_info (user_no, to_user_no)
SELECT a.user_no, b.user_no
FROM user a JOIN user b ON a.user_no <> b.user_no
WHERE CRC32(CONCAT(a.user_no, '>', b.user_no)) % 100 < 35;

-- ---------------------------------------------------------------------
-- 카드 100장: 대표 분류는 카드 번호 순서대로 돌아가며 배정 (card_no % 6)
-- ---------------------------------------------------------------------
INSERT INTO card (card_no, card_name, corp_name, img_url, ex_l_mth, ex_in_for, detail_link)
SELECT s.n,
       CONCAT(co.short, ' ', ct.card_nick, ELT(1 + (s.n - 1) DIV 30, '', ' 플러스', ' 프리미엄', ' II'), ' 카드'),
       co.name,
       CONCAT('/assets/card/', s.n, '.png'),
       ELT(1 + s.n % 3, '없음', '30만원 이상', '50만원 이상'),
       ELT(1 + s.n % 4, '국내 1만원 / 해외 1만2천원', '국내 1만5천원 / 해외 1만8천원',
                        '국내 2만원 / 해외 2만3천원', '국내 3만원 / 해외 3만원'),
       co.url
FROM seed_seq s
JOIN seed_corp co ON co.idx = (s.n - 1) % 10 + 1
JOIN seed_card_type ct ON ct.idx = (s.n - 1) % 6 + 1
WHERE s.n BETWEEN 1 AND 100;

-- 대표 분류 + 보조 분류 (두 식은 항상 다른 값)
INSERT INTO card_type (card_no, type)
SELECT cd.card_no, ct.name
FROM card cd JOIN seed_card_type ct ON ct.idx = (cd.card_no - 1) % 6 + 1
UNION ALL
SELECT cd.card_no, ct.name
FROM card cd JOIN seed_card_type ct ON ct.idx = (cd.card_no * 5) % 6 + 1;

INSERT INTO card_benefit (card_no, benefit, benefit_detail)
SELECT cd.card_no,
       CONCAT(ct.benefit, ' ', ELT(1 + cd.card_no % 3, '5%', '7%', '10%'), ' 할인'),
       CONCAT(ct.benefit, ' 결제 시 ', ELT(1 + cd.card_no % 3, '5%', '7%', '10%'),
              ' 청구 할인 (월 최대 ', ELT(1 + cd.card_no % 3, '1만원', '1만5천원', '2만원'),
              ', 전월 실적 ', cd.ex_l_mth, ')')
FROM card cd JOIN seed_card_type ct ON ct.idx = (cd.card_no - 1) % 6 + 1;

-- 회원 1명당 카드 2장: 주 소비 카테고리에 맞는 카드 1장 + 아무 카드 1장
-- (card_no = 분류 번호 + 6k 이면 그 분류가 대표인 카드)
INSERT IGNORE INTO card_by_user (user_no, card_no)
SELECT f.user_no, c.card_type_idx + 6 * ((f.user_no - 1) % 16)
FROM seed_user_fav f JOIN seed_category c ON c.idx = f.cat_idx
UNION ALL
SELECT user_no, (user_no * 13 + 5) % 100 + 1 FROM user;

-- ---------------------------------------------------------------------
-- 예금(01)·적금(02) 상품
-- ---------------------------------------------------------------------
INSERT INTO finance_company (kor_co_nm, img_url, company_url) VALUES
('국민은행',   '/assets/bank/kb.svg',      'https://www.kbstar.com'),
('신한은행',   '/assets/bank/shinhan.svg', 'https://www.shinhan.com'),
('우리은행',   '/assets/bank/woori.svg',   'https://www.wooribank.com'),
('하나은행',   '/assets/bank/hana.svg',    'https://www.kebhana.com'),
('농협은행',   '/assets/bank/nh.svg',      'https://banking.nonghyup.com'),
('중소기업은행', '/assets/bank/ibk.svg',   'https://www.ibk.co.kr'),
('카카오뱅크', '/assets/bank/kakao.svg',   'https://www.kakaobank.com'),
('토스뱅크',   '/assets/bank/toss.svg',    'https://www.tossbank.com');

INSERT INTO finance_product
    (kor_co_nm, finance_product_type, fin_prdt_nm, intr_rate, intr_rate2, join_member, join_way, save_trm, intr_rate_type_nm, spcl_cnd, mtrt_int)
VALUES
('국민은행',     '01', 'KB Star 정기예금',       2.80, 3.20, FALSE, '인터넷,스마트폰',        12, '단리', '급여이체 실적 보유 시 0.2%p, 첫 거래 고객 0.2%p', '만기 후 1개월 이내: 기본금리의 50%, 1개월 초과: 기본금리의 20%'),
('신한은행',     '01', '쏠편한 정기예금',         2.75, 3.15, FALSE, '스마트폰',               12, '단리', '신한 SOL 알림 동의 0.1%p, 마케팅 동의 0.3%p',      '만기 후 1개월 이내: 기본금리의 50%, 1개월 초과: 연 0.1%'),
('우리은행',     '01', 'WON플러스 예금',          2.90, 3.25, FALSE, '인터넷,스마트폰',        12, '단리', '우리WON뱅킹 가입 0.2%p, 오픈뱅킹 등록 0.15%p',     '만기 후 1개월 이내: 기본금리의 50%'),
('하나은행',     '01', '하나의정기예금',          2.85, 3.10, FALSE, '스마트폰',               12, '단리', '하나원큐 첫 가입 0.25%p',                          '만기 후 1개월 이내: 기본금리의 30%'),
('농협은행',     '01', 'NH올원e예금',             2.70, 3.30, FALSE, '인터넷,스마트폰',        12, '단리', 'NH올원뱅크 가입 0.3%p, 카드 실적 0.3%p',           '만기 후 3개월 이내: 기본금리의 50%'),
('중소기업은행', '01', 'IBK D-day 정기예금',      2.95, 3.35, FALSE, '영업점,인터넷,스마트폰', 12, '단리', '카드 결제 실적 0.2%p, 급여이체 0.2%p',             '만기 후 1개월 이내: 기본금리의 50%, 이후 연 0.2%'),
('카카오뱅크',   '01', '카카오뱅크 정기예금',     3.00, 3.00, FALSE, '스마트폰',               12, '단리', '없음',                                              '만기 후 연 0.1%'),
('토스뱅크',     '01', '토스뱅크 먼저 이자 받는 예금', 2.90, 3.10, FALSE, '스마트폰',          6,  '단리', '첫 예금 가입 0.2%p',                                '만기 후 연 0.1%'),
('국민은행',     '01', 'KB 청년 스타트 예금',     3.00, 3.80, TRUE,  '스마트폰',               12, '단리', '만 19~34세 청년 전용, 첫 거래 0.5%p, 급여이체 0.3%p', '만기 후 1개월 이내: 기본금리의 50%'),
('신한은행',     '01', '신한 My플러스 정기예금',  2.60, 3.40, FALSE, '영업점,인터넷,스마트폰', 24, '복리', '24개월 이상 유지 0.5%p, 자동이체 0.3%p',          '만기 후 1개월 이내: 기본금리의 50%'),
('하나은행',     '01', '하나 서민 행복 예금',     3.10, 3.70, TRUE,  '영업점',                 12, '단리', '기초생활수급자·차상위계층 전용, 공과금 이체 0.3%p', '만기 후 1개월 이내: 기본금리의 50%'),
('농협은행',     '01', 'NH 36개월 복리예금',      2.65, 3.05, FALSE, '영업점,인터넷',          36, '복리', '36개월 유지 시 0.4%p',                              '만기 후 3개월 이내: 기본금리의 50%'),
('국민은행',     '02', 'KB 내맘대로 적금',        3.00, 4.50, FALSE, '인터넷,스마트폰',        12, '단리', '자동이체 1.0%p, 첫 거래 고객 0.5%p',                '만기 후 1개월 이내: 기본금리의 50%'),
('신한은행',     '02', '신한 쏠메이트 적금',      3.10, 4.80, FALSE, '스마트폰',               12, '단리', '카드 결제 30만원 이상 1.2%p, 마케팅 동의 0.5%p',   '만기 후 1개월 이내: 기본금리의 50%'),
('우리은행',     '02', '우리 SUPER 주거래 적금',  3.20, 5.00, FALSE, '영업점,인터넷,스마트폰', 12, '단리', '급여이체 1.0%p, 우리카드 결제 0.8%p',              '만기 후 1개월 이내: 기본금리의 50%'),
('하나은행',     '02', '하나 급여하나 월복리 적금', 3.00, 4.60, FALSE, '스마트폰',             24, '복리', '급여이체 1.2%p, 하나원큐 가입 0.4%p',               '만기 후 1개월 이내: 기본금리의 50%'),
('농협은행',     '02', 'NH 청년도약 적금',        3.50, 6.00, TRUE,  '영업점,스마트폰',        36, '단리', '만 19~34세, 개인소득 7,500만원 이하, 급여이체 1.5%p', '만기 후 1개월 이내: 기본금리의 50%'),
('중소기업은행', '02', 'IBK 성실 적금',           3.30, 4.70, FALSE, '영업점,인터넷,스마트폰', 12, '단리', '12회 이상 납입 1.0%p, 카드 실적 0.4%p',            '만기 후 1개월 이내: 기본금리의 50%'),
('카카오뱅크',   '02', '카카오뱅크 26주 적금',    2.50, 7.00, FALSE, '스마트폰',               6,  '단리', '26주 연속 자동이체 성공 시 최대 4.5%p',             '만기 후 연 0.1%'),
('토스뱅크',     '02', '토스뱅크 키워봐요 적금',  3.00, 5.50, FALSE, '스마트폰',               12, '단리', '매주 자동이체 성공 2.0%p, 토스 체크카드 0.5%p',     '만기 후 연 0.1%'),
('우리은행',     '02', '우리 청년 희망 적금',     4.00, 6.50, TRUE,  '스마트폰',               24, '단리', '만 19~34세 청년 전용, 급여이체 1.5%p, 첫 거래 1.0%p', '만기 후 1개월 이내: 기본금리의 50%'),
('신한은행',     '02', '신한 매일 커피 적금',     2.80, 4.20, FALSE, '스마트폰',               6,  '단리', '매일 저축 성공 1.4%p',                              '만기 후 1개월 이내: 기본금리의 30%'),
('국민은행',     '02', 'KB 두근두근 여행 적금',   3.10, 4.40, FALSE, '스마트폰',               12, '단리', '여행 목표 달성 0.8%p, 카드 해외결제 0.5%p',         '만기 후 1개월 이내: 기본금리의 50%'),
('하나은행',     '02', '하나 도전 365 적금',      3.20, 5.20, FALSE, '스마트폰',               12, '복리', '걸음 수 목표 달성 1.5%p, 마케팅 동의 0.5%p',       '만기 후 1개월 이내: 기본금리의 50%');

-- 회원마다 가입한 상품 몇 개
INSERT INTO product_by_user (user_no, finance_product_no)
SELECT u.user_no, fp.finance_product_no
FROM user u JOIN finance_product fp
WHERE CRC32(CONCAT(u.user_no, '$', fp.finance_product_no)) % 100 < 12;

-- ---------------------------------------------------------------------
-- 소비 내역 2,400건 (회원당 200건)
--   50% 확률로 회원의 주 소비 카테고리, 나머지는 10개 카테고리 중 하나
--   결제 카드는 card_by_user 의 두 장 중 하나
-- ---------------------------------------------------------------------
INSERT INTO post (user_no, card_no, public_status, category, memo, amount, place, transaction_date)
SELECT t.u,
       IF(CRC32(CONCAT(t.n, '-card')) % 100 < 60,
          fc.card_type_idx + 6 * ((t.u - 1) % 16),
          (t.u * 13 + 5) % 100 + 1),
       CRC32(CONCAT(t.n, '-public')) % 100 < 80,
       c.name,
       IF(CRC32(CONCAT(t.n, '-memo')) % 100 < 25,
          ELT(1 + CRC32(CONCAT(t.n, '-memotext')) % 8,
              '오늘은 참았어야 했는데', '가성비 최고!', '친구랑 같이', '월급날 기념',
              '충동구매 인정합니다', '꼭 필요해서 샀어요', '이번 달 마지막 지출', '나를 위한 선물'),
          NULL),
       ROUND((c.lo + (CRC32(CONCAT(t.n, '-amount')) % 10000) / 10000 * (c.hi - c.lo)) / 100) * 100,
       SUBSTRING_INDEX(SUBSTRING_INDEX(c.places, '|', 1 + CRC32(CONCAT(t.n, '-place')) % 5), '|', -1),
       TIMESTAMP('2026-07-01')
           + INTERVAL (CRC32(CONCAT(t.n, '-day')) % 123) DAY
           + INTERVAL (480 + CRC32(CONCAT(t.n, '-minute')) % 840) MINUTE
FROM (
    SELECT s.n,
           f.user_no AS u,
           f.cat_idx AS fav_idx,
           IF(CRC32(CONCAT(s.n, '-fav')) % 100 < 50,
              f.cat_idx,
              1 + CRC32(CONCAT(s.n, '-cat')) % 10) AS cat_idx
    FROM seed_seq s
    JOIN seed_user_fav f ON f.user_no = s.n % 12 + 1
    WHERE s.n < 2400
) t
JOIN seed_category c ON c.idx = t.cat_idx
JOIN seed_category fc ON fc.idx = t.fav_idx
ORDER BY t.n;

-- 사진이 있는 게시글은 피드에 보이도록 공개 + 이미 지난 거래로 맞춤
INSERT INTO post_img (post_no, img_url)
SELECT post_no, CONCAT('/assets/peed/', post_no, '/', file)
FROM seed_peed
ORDER BY post_no, file;

UPDATE post
SET public_status = TRUE,
    transaction_date = IF(transaction_date > @now, transaction_date - INTERVAL 60 DAY, transaction_date)
WHERE post_no IN (SELECT post_no FROM post_img);

-- ---------------------------------------------------------------------
-- 실행 시각까지의 거래를 반영 (PostService.updateRenewStatusAndAmount 와 같은 결과)
-- ---------------------------------------------------------------------
UPDATE post SET renew_status = (transaction_date <= @now);
UPDATE user SET renew_time = @now;

INSERT INTO amount_by_date (user_no, transaction_date, amount)
SELECT user_no, DATE(transaction_date), SUM(amount)
FROM post WHERE renew_status
GROUP BY user_no, DATE(transaction_date);

INSERT INTO amount_by_category (user_no, category, amount)
SELECT user_no, category, SUM(amount)
FROM post WHERE renew_status
GROUP BY user_no, category;

-- 소비 성향 유형: 카테고리 금액 / 1점당 금액 이 가장 큰 카테고리 (UserService.setMbtiByCategory)
UPDATE user u
JOIN (
    SELECT a.user_no, c.mbti,
           ROW_NUMBER() OVER (PARTITION BY a.user_no ORDER BY a.amount / c.point DESC) AS rn
    FROM amount_by_category a
    JOIN seed_category c ON c.name = a.category
    WHERE c.point IS NOT NULL
) top ON top.user_no = u.user_no AND top.rn = 1
SET u.mbti_name = top.mbti;

-- ---------------------------------------------------------------------
-- 반응·댓글 (공개 + 반영된 게시글만, 사진 있는 글에 더 많이)
-- ---------------------------------------------------------------------
INSERT INTO great_or_stupid (user_no, post_no, isGreat)
SELECT u.user_no, p.post_no, CRC32(CONCAT(p.post_no, '?', u.user_no)) % 3 <> 0
FROM post p
JOIN user u ON u.user_no <> p.user_no
WHERE p.public_status AND p.renew_status
  AND CRC32(CONCAT(p.post_no, '/', u.user_no)) % 100
      < IF(EXISTS (SELECT 1 FROM post_img pi WHERE pi.post_no = p.post_no), 60, 8);

UPDATE post p
JOIN (
    SELECT post_no, SUM(isGreat) AS great, SUM(NOT isGreat) AS stupid
    FROM great_or_stupid GROUP BY post_no
) r ON r.post_no = p.post_no
SET p.great_count = r.great, p.stupid_count = r.stupid;

INSERT INTO comment (post_no, user_no, content)
SELECT p.post_no,
       (p.user_no + CRC32(CONCAT(p.post_no, '@', k.n)) % 11) % 12 + 1,
       ELT(1 + CRC32(CONCAT(p.post_no, '!', k.n)) % 10,
           '와 여기 어디예요?', '저도 여기 자주 가요', '이번 달 예산 괜찮으세요?', '잘 샀네요!',
           '이건 좀 참지 그랬어요 ㅋㅋ', '가성비 좋네요', '다음에 같이 가요!', '부럽다...',
           '통장 괜찮으세요?', '저도 사고 싶어요')
FROM post p
JOIN seed_seq k ON k.n < 3
WHERE p.public_status AND p.renew_status
  AND CRC32(CONCAT(p.post_no, '#', k.n)) % 100
      < IF(EXISTS (SELECT 1 FROM post_img pi WHERE pi.post_no = p.post_no), 70, 4)
ORDER BY p.post_no, k.n;

-- ---------------------------------------------------------------------
DROP TABLE seed_seq, seed_card_type, seed_category, seed_user_fav, seed_corp, seed_peed;
