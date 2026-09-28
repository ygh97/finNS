package com.finns.follow.controller;

import com.finns.follow.dto.FollowCountDTO;
import com.finns.follow.dto.FollowDTO;
import com.finns.follow.exception.AlreadyFollowingException;
import com.finns.follow.exception.FollowNotFoundException;
import com.finns.follow.service.FollowService;
import com.finns.member.dto.MemberDTO;
import com.finns.security.account.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 팔로우하는 주체는 항상 로그인한 사용자
@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{to_user_no}")
    public ResponseEntity<String> follow(@PathVariable int to_user_no, @AuthenticationPrincipal CustomUser user) {
        FollowDTO followDTO = new FollowDTO(user.getMember().getUser_no(), to_user_no);
        try {
            followService.follow(followDTO);
            return ResponseEntity.ok("Followed successfully");
        } catch (AlreadyFollowingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/follow/{to_user_no}")
    public ResponseEntity<String> unfollow(@PathVariable int to_user_no, @AuthenticationPrincipal CustomUser user) {
        FollowDTO followDTO = new FollowDTO(user.getMember().getUser_no(), to_user_no);
        try {
            followService.unfollow(followDTO);
            return ResponseEntity.ok("Unfollowed successfully");
        } catch (FollowNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/users/{user_no}/follower")
    public ResponseEntity<List<MemberDTO>> getFollowerList(@PathVariable int user_no, @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(followService.getFollowerList(user_no, user.getMember().getUser_no()));
    }

    @GetMapping("/users/{user_no}/following")
    public ResponseEntity<List<MemberDTO>> getFollowingList(@PathVariable int user_no, @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(followService.getFollowingList(user_no, user.getMember().getUser_no()));
    }

    @GetMapping("/users/{user_no}/followCounts")
    public ResponseEntity<FollowCountDTO> getFollowCounts(@PathVariable int user_no) {
        FollowCountDTO counts = followService.getFollowCounts(user_no);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/followingcheck")
    public ResponseEntity<Boolean> checkFollowing(@RequestParam long to_user_no, @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(followService.isFollowing(user.getMember().getUser_no(), to_user_no));
    }
}
