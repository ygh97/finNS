package com.finns.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {
    private Long cardNo; // 카드 번호
    private String cardName; // 카드명
    private String corpName; // 카드 회사명
    private String imgUrl; // 카드 이미지
    private String exLMth; // 전원 실적
    private String exInFor; // 연회비
    private String detailLink; // 카드 신청 페이지
    private String cardCategories;  // 카드 카테고리
    private String benefit; // 카드 요약 혜택
    private String benefitDetail;  // 카드 상세 혜택
}
