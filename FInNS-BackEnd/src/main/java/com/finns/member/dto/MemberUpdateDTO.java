package com.finns.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.finns.security.account.domain.MemberVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDTO {
    private int user_no;
    private String username;
    private String oldPassword; // 이전 비밀번호
    private String newPassword; // 새로운 비밀번호 (네이밍 변경)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    private String mbti_name;
    private String img_url;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date renew_time;

    private MultipartFile avatar; // 접근 제어자 추가

    // encodedPassword: 암호화된 새 비밀번호 또는 기존 비밀번호, imgUrl: 새 아바타 경로(없으면 null → 기존 유지)
    public MemberVO toVO(String encodedPassword, String imgUrl) {
        return MemberVO.builder()
                .user_no(user_no)
                .username(username)
                .password(encodedPassword)
                .birth(birth)
                .mbti_name(mbti_name)
                .img_url(imgUrl)
                .build();
    }
}
