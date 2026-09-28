package com.finns.post.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostResponseDTO {
    private long postNo;
    private long userNo;
    private String userName;
    private String userImgUrl;
    private long cardNo;
    private String cardName;
    private boolean publicStatus;
    private String category;
    private String memo;
    private long amount;
    private String place;
    private String transactionDate;
    private long greatCount;
    private long stupidCount;
    private boolean renewStatus;
    private List<String> imgUrls;
}
