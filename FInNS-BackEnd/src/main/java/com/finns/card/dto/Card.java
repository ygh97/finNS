package com.finns.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private long cardNo;
    private String cardName;
    private String corpName;
    private String imgUrl;
    private String exLMth;          // 전월실적
    private String exInFor;       // 연회비
    private String detailLink;     // 카드 신청 링크
    private String type;            // 카테고리명
    private String benefit;         // 요약혜택
    private String benefitDetail;  // 상세혜택
}
