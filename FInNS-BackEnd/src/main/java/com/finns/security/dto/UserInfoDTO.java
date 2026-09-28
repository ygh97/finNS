package com.finns.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.finns.security.account.domain.MemberVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private int user_no;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    private String mbti_name;
    private String img_url;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date renew_time;
    List<String> roles;

    public static UserInfoDTO of(MemberVO member) {
        if (member == null) {
            throw new IllegalArgumentException("MemberVO is null");
        }
        return new UserInfoDTO(
                member.getUser_no(),
                member.getUsername(),
                member.getBirth(),
                member.getMbti_name(),
                member.getImg_url(),
                member.getRenew_time(),
                member.getAuthList().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList())
        );
    }
}
