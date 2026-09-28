package com.finns.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long userNo;
    private String userName;
    private String birth;
    private String mbtiName;
    private String imgUrl;
    private String renewTime;
}
