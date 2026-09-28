-- =====================================================================
-- finNS 데이터베이스 스키마 (MariaDB 10.6+ / MySQL 8.0+)
--
-- 테이블·컬럼 이름은 MyBatis 매퍼(src/main/resources/com/finns/**/*.xml)와 맞춘다.
-- 적용:  mariadb -u root < db/schema.sql  (이어서 db/seed.sql 로 예시 데이터)
-- 주의:  finns DB가 이미 있으면 통째로 지우고 다시 만든다.
-- =====================================================================

DROP DATABASE IF EXISTS finns;
CREATE DATABASE finns CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE finns;

-- ---------------------------------------------------------------------
-- 회원
-- ---------------------------------------------------------------------
CREATE TABLE user (
    user_no     BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 해시',
    birth       DATE         NULL,
    mbti_name   VARCHAR(20)  NULL     COMMENT '소비 성향 유형 (com.finns.Mbti)',
    img_url     VARCHAR(255) NOT NULL DEFAULT '/assets/media/avatars/blank.png',
    renew_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '소비 내역을 마지막으로 반영한 시각',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_no),
    UNIQUE KEY uk_user_username (username),
    KEY idx_user_mbti (mbti_name, renew_time)
) ENGINE = InnoDB;

CREATE TABLE user_auth (
    username   VARCHAR(50) NOT NULL,
    authority  VARCHAR(50) NOT NULL,
    PRIMARY KEY (username, authority),
    CONSTRAINT fk_user_auth_user FOREIGN KEY (username) REFERENCES user (username)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE follow_info (
    user_no     BIGINT   NOT NULL COMMENT '팔로우하는 사람',
    to_user_no  BIGINT   NOT NULL COMMENT '팔로우 받는 사람',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_no, to_user_no),
    KEY idx_follow_to_user (to_user_no),
    CONSTRAINT chk_follow_not_self CHECK (user_no <> to_user_no),
    CONSTRAINT fk_follow_user    FOREIGN KEY (user_no)    REFERENCES user (user_no) ON DELETE CASCADE,
    CONSTRAINT fk_follow_to_user FOREIGN KEY (to_user_no) REFERENCES user (user_no) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE recent_user (
    user_no     BIGINT   NOT NULL COMMENT '조회한 사람',
    to_user_no  BIGINT   NOT NULL COMMENT '조회된 사람',
    view_time   DATETIME NOT NULL,
    PRIMARY KEY (user_no, to_user_no),
    KEY idx_recent_user_time (user_no, view_time),
    CONSTRAINT fk_recent_user    FOREIGN KEY (user_no)    REFERENCES user (user_no) ON DELETE CASCADE,
    CONSTRAINT fk_recent_to_user FOREIGN KEY (to_user_no) REFERENCES user (user_no) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 카드
-- ---------------------------------------------------------------------
CREATE TABLE card (
    card_no      BIGINT       NOT NULL AUTO_INCREMENT,
    card_name    VARCHAR(100) NOT NULL,
    corp_name    VARCHAR(50)  NOT NULL,
    img_url      VARCHAR(255) NULL,
    ex_l_mth     VARCHAR(50)  NULL COMMENT '전월 실적 조건',
    ex_in_for    VARCHAR(100) NULL COMMENT '연회비',
    detail_link  VARCHAR(500) NULL,
    PRIMARY KEY (card_no)
) ENGINE = InnoDB;

-- 카드 혜택 분류 (카드 1장에 여러 개). 소비 카테고리와는 다른 6분류이며
-- 소비 카테고리 → 카드 분류 변환은 CardService.matchingCategory 가 맡는다.
CREATE TABLE card_type (
    card_no  BIGINT      NOT NULL,
    type     VARCHAR(20) NOT NULL,
    PRIMARY KEY (card_no, type),
    KEY idx_card_type_type (type, card_no),
    CONSTRAINT chk_card_type_type CHECK (type IN ('식비', '여가', '교통', '의료', '통신', '교육')),
    CONSTRAINT fk_card_type_card FOREIGN KEY (card_no) REFERENCES card (card_no) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 카드 대표 혜택 (매퍼가 카드별 한 줄을 전제하므로 카드당 1행)
CREATE TABLE card_benefit (
    card_no         BIGINT       NOT NULL,
    benefit         VARCHAR(255) NOT NULL,
    benefit_detail  TEXT         NULL,
    PRIMARY KEY (card_no),
    CONSTRAINT fk_card_benefit_card FOREIGN KEY (card_no) REFERENCES card (card_no) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE card_by_user (
    user_no  BIGINT NOT NULL,
    card_no  BIGINT NOT NULL,
    PRIMARY KEY (user_no, card_no),
    KEY idx_card_by_user_card (card_no),
    CONSTRAINT fk_card_by_user_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE,
    CONSTRAINT fk_card_by_user_card FOREIGN KEY (card_no) REFERENCES card (card_no) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 소비 내역(게시글)
--   카드 거래가 미리 들어와 있고(renew_status = false),
--   사용자가 갱신하면 transaction_date <= 현재 시각인 것만 공개(renew_status = true)되며
--   그 금액이 amount_by_date / amount_by_category 에 누적된다. (PostService.updateRenewStatusAndAmount)
-- ---------------------------------------------------------------------
CREATE TABLE post (
    post_no           BIGINT        NOT NULL AUTO_INCREMENT,
    user_no           BIGINT        NOT NULL,
    card_no           BIGINT        NULL,
    public_status     BOOLEAN       NOT NULL DEFAULT TRUE,
    category          VARCHAR(20)   NOT NULL,
    memo              VARCHAR(500)  NULL,
    amount            INT UNSIGNED  NOT NULL,
    place             VARCHAR(100)  NOT NULL,
    transaction_date  DATETIME      NOT NULL,
    great_count       INT UNSIGNED  NOT NULL DEFAULT 0,
    stupid_count      INT UNSIGNED  NOT NULL DEFAULT 0,
    renew_status      BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (post_no),
    KEY idx_post_user_date (user_no, renew_status, transaction_date),
    KEY idx_post_user_category (user_no, category),
    KEY idx_post_public_great (public_status, great_count),
    CONSTRAINT chk_post_category CHECK (category IN
        ('식비 · 카페', '쇼핑', '미용', '의료', '통신', '교통', '문화 · 여행', '교육', '술 · 유흥', '기타')),
    CONSTRAINT fk_post_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE,
    CONSTRAINT fk_post_card FOREIGN KEY (card_no) REFERENCES card (card_no) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE post_img (
    post_img_no  BIGINT       NOT NULL AUTO_INCREMENT,
    post_no      BIGINT       NOT NULL,
    img_url      VARCHAR(255) NOT NULL,
    PRIMARY KEY (post_img_no),
    KEY idx_post_img_post (post_no),
    CONSTRAINT fk_post_img_post FOREIGN KEY (post_no) REFERENCES post (post_no) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE comment (
    comment_no  BIGINT        NOT NULL AUTO_INCREMENT,
    post_no     BIGINT        NOT NULL,
    user_no     BIGINT        NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (comment_no),
    KEY idx_comment_post (post_no, comment_no),
    CONSTRAINT fk_comment_post FOREIGN KEY (post_no) REFERENCES post (post_no) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 잘했어요(isGreat = true) / 멍청해요(false): 사용자·게시글당 하나
CREATE TABLE great_or_stupid (
    user_no  BIGINT  NOT NULL,
    post_no  BIGINT  NOT NULL,
    isGreat  BOOLEAN NOT NULL,
    PRIMARY KEY (user_no, post_no),
    KEY idx_great_or_stupid_post (post_no),
    CONSTRAINT fk_gos_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE,
    CONSTRAINT fk_gos_post FOREIGN KEY (post_no) REFERENCES post (post_no) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 집계 (갱신된 소비 내역의 누적 금액)
-- ---------------------------------------------------------------------
CREATE TABLE amount_by_date (
    user_no           BIGINT NOT NULL,
    transaction_date  DATE   NOT NULL,
    amount            BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (user_no, transaction_date),
    CONSTRAINT fk_amount_by_date_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE amount_by_category (
    user_no   BIGINT      NOT NULL,
    category  VARCHAR(20) NOT NULL,
    amount    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (user_no, category),
    CONSTRAINT fk_amount_by_category_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 예금·적금 상품
-- ---------------------------------------------------------------------
CREATE TABLE finance_company (
    kor_co_nm    VARCHAR(50)  NOT NULL,
    img_url      VARCHAR(255) NULL,
    company_url  VARCHAR(255) NULL,
    PRIMARY KEY (kor_co_nm)
) ENGINE = InnoDB;

CREATE TABLE finance_product (
    finance_product_no    BIGINT        NOT NULL AUTO_INCREMENT,
    kor_co_nm             VARCHAR(50)   NOT NULL,
    finance_product_type  CHAR(2)       NOT NULL COMMENT '01 예금, 02 적금',
    fin_prdt_nm           VARCHAR(100)  NOT NULL,
    intr_rate             DECIMAL(5, 2) NOT NULL COMMENT '기본 금리(%)',
    intr_rate2            DECIMAL(5, 2) NOT NULL COMMENT '최고 우대 금리(%)',
    join_member           BOOLEAN       NOT NULL DEFAULT FALSE COMMENT 'true = 가입 대상 제한 있음',
    join_way              VARCHAR(100)  NULL,
    save_trm              INT           NOT NULL COMMENT '저축 기간(개월)',
    intr_rate_type_nm     VARCHAR(10)   NOT NULL COMMENT '단리 / 복리',
    spcl_cnd              TEXT          NULL COMMENT '우대 조건',
    mtrt_int              TEXT          NULL COMMENT '만기 후 이자율',
    PRIMARY KEY (finance_product_no),
    KEY idx_finance_product_type_rate (finance_product_type, intr_rate2),
    KEY idx_finance_product_company (kor_co_nm),
    CONSTRAINT chk_finance_product_type CHECK (finance_product_type IN ('01', '02')),
    CONSTRAINT chk_finance_product_rate CHECK (intr_rate2 >= intr_rate),
    CONSTRAINT fk_finance_product_company FOREIGN KEY (kor_co_nm) REFERENCES finance_company (kor_co_nm)
        ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE product_by_user (
    user_no             BIGINT NOT NULL,
    finance_product_no  BIGINT NOT NULL,
    PRIMARY KEY (user_no, finance_product_no),
    KEY idx_product_by_user_product (finance_product_no),
    CONSTRAINT fk_product_by_user_user    FOREIGN KEY (user_no)            REFERENCES user (user_no) ON DELETE CASCADE,
    CONSTRAINT fk_product_by_user_product FOREIGN KEY (finance_product_no) REFERENCES finance_product (finance_product_no) ON DELETE CASCADE
) ENGINE = InnoDB;
