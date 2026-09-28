package com.finns.comment.controller;

import com.finns.comment.dto.CommentDTO;
import com.finns.comment.service.CommentService;
import com.finns.security.account.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{post_no}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostNo(@PathVariable("post_no")Long postNo) {
        return ResponseEntity.ok(commentService.getCommentList(postNo));
    }
    // 새로운 댓글 추가 - 작성자는 로그인한 사용자
    @PostMapping
    public void addComment(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal CustomUser user) {
        commentDTO.setUserNo((long) user.getMember().getUser_no());
        commentService.addComment(commentDTO);
    }
}
