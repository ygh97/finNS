package com.finns.recentUser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertRecentUserDTO {
    private long userNo;
    private long toUserNo;
    private LocalDateTime viewTime;
}
