package com.finns.finance.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Finance {
    private long financeProductNo; // 금융상품번호
    private String korCoNm; // 금융회사명
    private String financeProductType; // 금융상품타입
    private String finPrdtNm; // 금융상품명
    private BigDecimal intrRate2; // 최고 금리
    private BigDecimal  intrRate; // 기본 금리
    private boolean joinMember; // 가입 대상
    private String joinWay; // 가입 방법
    private int saveTrm; // 가입 기간(저축 기간)
    private String intrRateTypeNm; // 이자 계산 방식
    private String spclCnd; // 우대조건
    private String mtrtInt; // 만기후 이자율
}
