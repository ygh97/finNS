package com.finns;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Mbti {
    FOOD_CAFE("식비 · 카페", "먹는게제일좋아형", 100),
    SHOPPING("쇼핑", "이것도저것도내꺼형", 70),
    BEAUTY("미용", "자기관리마니아형", 50),
    MEDICAL("의료", "마이아파형", 60),
    COMMUNICATION("통신", "통신보안형", 60),
    TRANSPORT("교통", "뚜벅초형", 20),
    CULTURE_TRAVEL("문화 · 여행", "배낭을매고형", 100),
    EDUCATION("교육", "아인슈타인형", 50),
    ALCOHOL_ENTERTAINMENT("술 · 유흥", "술술들어간다형", 100);
    // "기타" 카테고리는 MBTI 유형이 없으므로 점수 계산에서 제외 (calculatePoints가 0 반환)

    private final String categoryName;
    private final String mbtiName;
    private final int amountPerPoint; // 1점 당 금액

    // 카테고리 이름으로 Mbti를 찾을 수 있는 Map
    private static final Map<String, Mbti> CATEGORY_MAP = new HashMap<>();

    // static 블록을 사용해 enum 값을 Map에 미리 저장
    static {
        for (Mbti category : values()) {
            CATEGORY_MAP.put(category.categoryName, category);
        }
    }

    // 카테고리로 mbtiName을 찾는 메서드
    public static String getMbtiNameByCategory(String categoryName) {
        Mbti mbti = CATEGORY_MAP.get(categoryName);
        if (mbti != null) {
            return mbti.getMbtiName();
        }
        throw new IllegalArgumentException("Unknown category: " + categoryName);
    }

    // 카테고리와 그 카테고리에서 총 사용 금액을 입력 받아 점수를 반환, MBTI 유형이 없는 카테고리(기타 등)는 0점
    public static double calculatePoints(String categoryName, double totalAmount) {
        Mbti mbti = CATEGORY_MAP.get(categoryName);
        return mbti != null ? totalAmount / mbti.getAmountPerPoint() : 0;
    }
}