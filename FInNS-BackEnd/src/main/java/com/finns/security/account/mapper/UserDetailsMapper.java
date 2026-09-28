package com.finns.security.account.mapper;

import com.finns.security.account.domain.MemberVO;

public interface UserDetailsMapper {
    public MemberVO get(String username);
}
