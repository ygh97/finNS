package com.finns.member.mapper;

import com.finns.member.dto.ChangePasswordDTO;
import com.finns.security.account.domain.AuthVO;
import com.finns.security.account.domain.MemberVO;

public interface MemberMapper {
    MemberVO get(String username);

    MemberVO checkUsername(String username); // id 중복 체크시 사용

    int insert(MemberVO member); // 회원 정보 추가

    int insertAuth(AuthVO auth); //회원 권한 정보 추가

    int update(MemberVO member);

    int updatePassword(ChangePasswordDTO changePasswordDTO);

}
