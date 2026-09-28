package com.finns.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChangeRenewStatusDTO {
    private Long userNo;
    private LocalDateTime now;
}
