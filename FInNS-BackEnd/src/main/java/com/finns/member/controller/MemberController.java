package com.finns.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.finns.member.dto.ChangePasswordDTO;
import com.finns.member.dto.MemberDTO;
import com.finns.member.dto.MemberJoinDTO;
import com.finns.member.dto.MemberUpdateDTO;
import com.finns.member.service.MemberService;
import com.finns.security.account.domain.CustomUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberController {

    // @Qualifier 추가하여 사용할 구현체 지정
    private final MemberService service;

    public MemberController(@Qualifier("memberServiceImpl") MemberService service) {
        this.service = service;
    }


    @GetMapping("/checkusername/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        return ResponseEntity.ok().body(service.checkDuplicate(username));
    }

    @PostMapping("")
    public ResponseEntity<MemberDTO> join(MemberJoinDTO member) {
        return ResponseEntity.ok(service.join(member));
    }

    @PutMapping("/{username}")
    public ResponseEntity<MemberDTO> update(
            @PathVariable String username,
            @ModelAttribute MemberUpdateDTO member,
            @AuthenticationPrincipal CustomUser user) {
        // 본인 정보만 수정 가능
        if (!user.getUsername().equals(username)) {
            throw new AccessDeniedException("본인 정보만 수정할 수 있습니다.");
        }
        member.setUsername(username);

        // `service.update` 메서드를 통해 프로필 및 비밀번호 업데이트 처리
        return ResponseEntity.ok(service.update(member));
    }



//    @PutMapping("/{username}/changepassword")
//    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
//        service.changePassword(changePasswordDTO);
//        return ResponseEntity.ok().build();
//    }
}
