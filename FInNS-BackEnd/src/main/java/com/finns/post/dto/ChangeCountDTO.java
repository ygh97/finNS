package com.finns.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ChangeCountDTO {
    private Long no;
    private boolean isGreatCount;
    private int addNum;
}
