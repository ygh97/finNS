package com.finns.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetMbtiDTO {
    private long userNo;
    private String mbtiName;
}
