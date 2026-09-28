package com.finns.amountByCategory.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmountByCategory {
    private String category;
    private long userNo;
    private long amount;
}
