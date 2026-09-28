package com.finns.recentUser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentUserResponseDTO {
    private long userNo;
    private String userName;
    private String imgUrl;
    private boolean isFollow;
}
