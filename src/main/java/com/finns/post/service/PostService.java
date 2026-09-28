package com.finns.post.service;

import com.finns.amountByCategory.mapper.AmountByCategoryMapper;
import com.finns.post.dto.*;
import com.finns.amountByDate.mapper.AmountByDateMapper;
import com.finns.post.mapper.PostMapper;
import com.finns.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource({"classpath:/application.properties"})
@Transactional(readOnly = true)
public class PostService {
    private final PostMapper postMapper;
    private final AmountByDateMapper amountByDateMapper;
    private final AmountByCategoryMapper amountByCategoryMapper;
    private final UserMapper userMapper;

    // 비공개 게시글은 작성자 본인만 조회 가능
    public PostResponseDTO getPost(Long no, long viewerNo) {
        // 게시글 정보 가져오기
        PostResponseDTO postResponse = Optional.ofNullable(postMapper.selectOne(no))
                .filter(post -> post.isPublicStatus() || post.getUserNo() == viewerNo)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        // 이미지 URL 리스트 가져오기
        List<String> imgUrls = postMapper.selectImagesByPostNo(no);
        postResponse.setImgUrls(imgUrls); // 이미지 URL 리스트 설정

        return postResponse;
    }

    public List<PostResponseDTO> getPostsByUserAndDateAndIsPublic(PostRequestByDateDTO postRequestByDateDTO) {
        return Optional.ofNullable(postMapper.selectAllByUserAndDateAndIsPublic(postRequestByDateDTO))
                .orElseThrow(NoSuchElementException::new);
    }

    public List<PostResponseDTO> getPostsByUserAndCategoryAndIsPublic(PostRequestByCategoryDTO postRequestByCategoryDTO) {
        return Optional.ofNullable(postMapper.selectAllByUserAndCategoryAndIsPublic(postRequestByCategoryDTO))
                .orElseThrow(NoSuchElementException::new);
    }

    // 작성자 본인의 게시글만 수정됨
    @Transactional
    public void updatePostDetail(UpdatePostDetailDTO updatePostDetailDTO) {
        if (postMapper.updatePost(updatePostDetailDTO) == 0) {
            throw new NoSuchElementException("게시글이 없거나 수정 권한이 없습니다.");
        }
    }

    @Transactional
    public void updateRenewStatusAndAmount(ChangeRenewStatusDTO changeRenewStatusDTO) {
        // 1. 갱신된 post의 date, category, userNo, amount 가져오기
        List<UpdateAmountDTO> updatedPosts = postMapper.selectUpdatedRenewPost(changeRenewStatusDTO);

        // 2. 갱신된 각 데이터에 대해 날짜별, 카테고리별 amount 업데이트
        for (UpdateAmountDTO post : updatedPosts) {
            amountByDateMapper.upsertByDateAndUser(post);
            amountByCategoryMapper.upsertByCategoryAndUser(post);
        }

        // 3. renew_status 업데이트
        postMapper.updateRenewStatusByUser(changeRenewStatusDTO);

        // 4. user의 renew_time 업데이트
        userMapper.updateRenewTime(changeRenewStatusDTO);
    }

    public Long getCountByUser(Long userNo) {
        return Optional.of(postMapper.selectCountByUser(userNo))
                .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void reversePublicStatus(Long no, long userNo) {
        if (postMapper.updatePublicStatus(no, userNo) == 0) {
            throw new NoSuchElementException("게시글이 없거나 수정 권한이 없습니다.");
        }
    }
    // 전체 중복 제거된 post_no 가져오기
    public List<Long> getDistinctPostNos() {
        return postMapper.selectDistinctPostNos();
    }

    public List<PostResponseDTO> getTop3PostsByGreatCount() {
        return postMapper.getTop3PostsByGreatCount();
    }
}
