package com.finns.user.controller;

import com.finns.security.account.domain.CustomUser;
import com.finns.user.dto.*;
import com.finns.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "UserController", tags = "사용자 정보")
@PropertySource({"classpath:/application.properties"})
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{no}")
    public ResponseEntity<User> UserInfo(@PathVariable("no") Long userNo) {
        User user = userService.getUser(userNo);
        return ResponseEntity.ok().body(user);
    }

    // 로그인한 사용자를 제외한 전체 사용자와 팔로우 여부
    @GetMapping("/users/search")
    public ResponseEntity<List<SearchUserDTO>> SearchUsers(@AuthenticationPrincipal CustomUser user) {
        List<SearchUserDTO> users = userService.getUsers((long) user.getMember().getUser_no());
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/users/{no}/recommend5")
    public ResponseEntity<List<UserRecommendResponseDTO>> recommend5Users(@PathVariable("no") Long userNo) {
        User user = userService.getUser(userNo);
        UserRecommendRequestDTO userRecommendRequestDTO = new UserRecommendRequestDTO(userNo, user.getMbtiName());

        List<UserRecommendResponseDTO> recommendUsers = userService.getRecommend5ByMbti(userRecommendRequestDTO);
        return ResponseEntity.ok().body(recommendUsers);
    }

    @PutMapping("/users/{no}/mbti")
    public ResponseEntity<?> analysisMbti(@PathVariable("no") Long userNo, @AuthenticationPrincipal CustomUser user) {
        if (userNo != user.getMember().getUser_no()) {
            throw new AccessDeniedException("본인 MBTI만 진단할 수 있습니다.");
        }
        userService.setMbtiByCategory(userNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/top3/{year}/{month}")
    public ResponseEntity<List<UserTop3DTO>> top3ForAmountByDate(@PathVariable("year") int year, @PathVariable("month") int month) {
        YearAndMonthDTO yearAndMonthDTO = new YearAndMonthDTO(year, month);
        List<UserTop3DTO> top3Users = userService.getTop3ForAmountByDate(yearAndMonthDTO);
        return ResponseEntity.ok().body(top3Users);
    }
}
