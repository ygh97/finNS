package com.finns.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.finns.security.account.domain.MemberVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinDTO {
    private int user_no;
    private String username;
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    private String mbti_name;
    private String img_url;
    private Date renew_time;

    MultipartFile avatar;

    public MemberVO toVO(){
        return MemberVO.builder()
                .username(username)
                .password(password)
                .birth(birth)
                .build();
    }
}
