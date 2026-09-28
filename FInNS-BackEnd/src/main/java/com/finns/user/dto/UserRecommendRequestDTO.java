package com.finns.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecommendRequestDTO {
    private long userNo;
    private String mbtiName;
}


