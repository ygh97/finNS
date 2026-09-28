package com.finns.member.service;

import com.finns.member.dto.ChangePasswordDTO;
import com.finns.member.dto.MemberDTO;
import com.finns.member.dto.MemberJoinDTO;
import com.finns.member.dto.MemberUpdateDTO;

public interface MemberService {
    boolean checkDuplicate(String username);

    MemberDTO get(String username);

    MemberDTO join(MemberJoinDTO member);

    MemberDTO update(MemberUpdateDTO member);

//    void changePassword(ChangePasswordDTO changePassword);
}
