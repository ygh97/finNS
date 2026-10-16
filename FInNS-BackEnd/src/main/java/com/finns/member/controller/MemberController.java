package com.finns.member.controller;

import lombok.RequiredArgsConstructor;
import com.finns.member.dto.MemberDTO;
import com.finns.member.dto.MemberJoinDTO;
import com.finns.member.dto.MemberUpdateDTO;
import com.finns.member.service.MemberService;
import com.finns.security.account.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    // 배포 사이트의 체험 계정 - 누구나 쓰므로 비밀번호·사진을 바꾸지 못하게 막는다
    private static final String DEMO_USERNAME = "demo";

    private final MemberService service;

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
        if (DEMO_USERNAME.equals(username)) {
            throw new AccessDeniedException("체험 계정은 정보를 수정할 수 없습니다.");
        }
        member.setUsername(username);
        return ResponseEntity.ok(service.update(member));
    }
}
