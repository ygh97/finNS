package com.finns.finance.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Card {
    private Long cardNo; // 카드 번호
    private String cardName; // 카드명
    private String corpName; // 카드 회사명
    private String imgUrl; // 카드 이미지
    private String exLMth; // 전원 실적
    private String exInFor; // 연회비
    private String detailLink; // 카드 신청 페이지
}
