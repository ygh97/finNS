package com.finns.post.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdatePostDetailDTO {
    private long postNo;
    private long userNo; // 로그인한 사용자, 서버에서 설정
    private boolean publicStatus;
    private String category;
    private String memo;
}
