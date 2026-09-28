package com.finns.security.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberVO {
    private int user_no;
    private String username;
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    private String mbti_name;
    private String img_url;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date renew_time;
    private boolean following;
    private List<AuthVO> authList;
    private String oldPassword; // 이전 비밀번호
    private String newPassword; // 새로운 비밀번호 (네이밍 변경)

    UserDetails details;
}