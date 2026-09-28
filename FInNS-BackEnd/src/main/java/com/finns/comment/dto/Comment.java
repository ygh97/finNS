package com.finns.comment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment {
    private Long commentNo;
    private Long postNo;
    private Long userNo;
    private String content;
    private String userName;
    private String imgUrl;
}
