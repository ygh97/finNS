package com.finns.member.dto;

import com.finns.security.account.domain.MemberVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

// 프로필 수정 요청 (multipart) - 비밀번호와 프로필 사진만 바꿀 수 있다
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDTO {
    private String username;
    private String oldPassword;
    private String newPassword;
    private MultipartFile avatar;

    // encodedPassword: 암호화된 새 비밀번호 또는 기존 비밀번호, imgUrl: 새 사진 경로(없으면 null → 기존 유지)
    public MemberVO toVO(String encodedPassword, String imgUrl) {
        return MemberVO.builder()
                .username(username)
                .password(encodedPassword)
                .img_url(imgUrl)
                .build();
    }
}
