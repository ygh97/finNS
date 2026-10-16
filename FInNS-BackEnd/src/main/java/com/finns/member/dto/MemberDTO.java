package com.finns.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.finns.security.account.domain.MemberVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

    private int user_no;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    private String mbti_name;
    private String img_url;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date renew_time;
    private boolean following;
    private List<String> authList;

    public static MemberDTO of(MemberVO m) {
        return MemberDTO.builder()
                .user_no(m.getUser_no())
                .username(m.getUsername())
                .birth(m.getBirth())
                .mbti_name(m.getMbti_name())
                .img_url(m.getImg_url())
                .renew_time(m.getRenew_time())
                .authList(m.getAuthList().stream().map(a->a.getAuthority()).toList())
                .build();
    }
}
