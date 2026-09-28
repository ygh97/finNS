package com.finns.recentUser.controller;

import com.finns.recentUser.dto.*;
import com.finns.recentUser.service.RecentUserService;
import com.finns.security.account.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// 최근 검색한 사용자는 로그인한 사용자 본인 기준
@Slf4j
@RestController
@RequiredArgsConstructor
@PropertySource({"classpath:/application.properties"})
public class RecentUserController {

    private final RecentUserService recentUserService;

    @GetMapping("/recentUser")
    public ResponseEntity<List<RecentUserResponseDTO>> recentUsers(@AuthenticationPrincipal CustomUser user) {
        List<RecentUserResponseDTO> recentUsers = recentUserService.getRecentUser((long) user.getMember().getUser_no());
        return ResponseEntity.ok().body(recentUsers);
    }

    @PutMapping("/recentUser/{toUserNo}")
    public ResponseEntity<?> newRecentUser(@PathVariable("toUserNo") Long toUserNo, @AuthenticationPrincipal CustomUser user) {
        InsertRecentUserDTO insertRecentUserDTO = new InsertRecentUserDTO(user.getMember().getUser_no(), toUserNo, LocalDateTime.now());
        recentUserService.insertRecentUser(insertRecentUserDTO);
        return ResponseEntity.ok().build();
    }
}
