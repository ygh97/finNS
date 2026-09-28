package com.finns.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAmountDTO {
    private LocalDate transactionDate;
    private String category;
    private Long userNo;
    private Long amount;
}
