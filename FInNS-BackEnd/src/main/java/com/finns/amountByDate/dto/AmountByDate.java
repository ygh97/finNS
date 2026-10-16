package com.finns.amountByDate.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmountByDate {
    private String transactionDate;
    private long userNo;
    private long amount;
}
