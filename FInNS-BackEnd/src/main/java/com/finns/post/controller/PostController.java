package com.finns.post.controller;

import com.finns.post.dto.*;
import com.finns.post.service.PostService;
import com.finns.security.account.domain.CustomUser;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "PostController", tags = "게시글 정보")
@PropertySource({"classpath:/application.properties"})
public class PostController {

    private final PostService postService;

    @GetMapping("/posts/{no}")
    public ResponseEntity<PostResponseDTO> postInfo(@PathVariable("no") Long no, @AuthenticationPrincipal CustomUser user) {
        PostResponseDTO post = postService.getPost(no, user.getMember().getUser_no());
        return ResponseEntity.ok().body(post);
    }

    // 다른 사람의 소비내역은 공개된 것만 조회
    @PostMapping("/posts/byDate")
    public ResponseEntity<List<PostResponseDTO>> postsInfoByUserAndDateAndIsPublic(@RequestBody PostRequestByDateDTO postRequestByDateDTO,
                                                                                   @AuthenticationPrincipal CustomUser user) {
        if (!isMe(user, postRequestByDateDTO.getUserNo())) {
            postRequestByDateDTO.setIsOnlyPublic(true);
        }
        List<PostResponseDTO> posts = postService.getPostsByUserAndDateAndIsPublic(postRequestByDateDTO);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts/byCategory")
    public ResponseEntity<List<PostResponseDTO>> postsInfoByUserAndCategoryAndIsPublic(@RequestBody PostRequestByCategoryDTO postRequestByCategoryDTO,
                                                                                       @AuthenticationPrincipal CustomUser user) {
        if (!isMe(user, postRequestByCategoryDTO.getUserNo())) {
            postRequestByCategoryDTO.setIsOnlyPublic(true);
        }
        List<PostResponseDTO> posts = postService.getPostsByUserAndCategoryAndIsPublic(postRequestByCategoryDTO);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/posts/{no}/update")
    public ResponseEntity<?> updatePost(@PathVariable("no") Long no, @RequestBody UpdatePostDetailDTO updatePostDetailDTO,
                                        @AuthenticationPrincipal CustomUser user) {
        updatePostDetailDTO.setPostNo(no);
        updatePostDetailDTO.setUserNo(user.getMember().getUser_no());
        postService.updatePostDetail(updatePostDetailDTO);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{no}/renew")
    public ResponseEntity<?> renewPosts(@PathVariable("no") Long userNo, @AuthenticationPrincipal CustomUser user) {
        if (!isMe(user, userNo)) {
            throw new AccessDeniedException("본인 소비내역만 갱신할 수 있습니다.");
        }
        ChangeRenewStatusDTO changeRenewStatusDTO = new ChangeRenewStatusDTO(userNo, LocalDateTime.now());
        postService.updateRenewStatusAndAmount(changeRenewStatusDTO);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{no}/posts/count")
    public ResponseEntity<Long> countPosts(@PathVariable("no") Long userNo) {
        Long count = postService.getCountByUser(userNo);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/posts/{no}/togglePublicStatus")
    public ResponseEntity<Long> togglePublicStatus(@PathVariable("no") Long no, @AuthenticationPrincipal CustomUser user) {
        postService.reversePublicStatus(no, user.getMember().getUser_no());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/images/distinct")
    public ResponseEntity<List<Long>> getDistinctPostNos() {
        List<Long> distinctPostNos = postService.getDistinctPostNos();
        return ResponseEntity.ok(distinctPostNos);
    }

    @GetMapping("/posts/top3")
    public List<PostResponseDTO> getTop3PostsByGreatCount() {
        return postService.getTop3PostsByGreatCount();
    }

    private boolean isMe(CustomUser user, Long userNo) {
        return userNo != null && userNo == user.getMember().getUser_no();
    }
}
