package com.finns.finance.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FinanceCount {
    private Long financeProductNo;
    private Long productCount;
}
