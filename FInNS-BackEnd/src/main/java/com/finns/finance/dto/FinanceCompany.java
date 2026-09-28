package com.finns.finance.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FinanceCompany {
    private String korCoNm; // 금융회사명
    private String imgUrl; // 은행 로고 이미지 힝크
}
