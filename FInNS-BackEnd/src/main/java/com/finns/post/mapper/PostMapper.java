package com.finns.post.mapper;

import com.finns.post.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper {
    // 게시글 하나 가져오기
    PostResponseDTO selectOne(Long no);

    // 게시글들 가져오기(특정 유저, 특정 날짜, 공개 범위)
    List<PostResponseDTO> selectAllByUserAndDateAndIsPublic(PostRequestByDateDTO postRequestByDateDTO);

    List<PostResponseDTO> selectAllByUserAndCategoryAndIsPublic(PostRequestByCategoryDTO postRequestByCategoryDTO);

    // 갱신 버튼 클릭 시(멤버의 마지막 갱신날짜 이후 ~ 현재까지의 게시글들의 renew_status를 true로 수정)
    void updateRenewStatusByUser(ChangeRenewStatusDTO changeRenewStatusDTO);

    List<UpdateAmountDTO> selectUpdatedRenewPost(ChangeRenewStatusDTO changeRenewStatusDTO);

    long selectCountByUser(Long userNo);

    // 작성자 본인 게시글만 변경, 변경된 행 수 반환
    int updatePublicStatus(@Param("postNo") Long no, @Param("userNo") long userNo);

    int updatePost(UpdatePostDetailDTO updatePostDetailDTO);     // 공개 여부, 카테고리, 메모만 수정

    List<Long> selectDistinctPostNos();

    List<String> selectImagesByPostNo(Long no);

    // 게시글의 great_count 증가 메서드
    void incrementGreatCount(Long postNo);

    // 게시글의 stupid_count 증가 메서드
    void incrementStupidCount(Long postNo);

    // 게시글의 great_count 감소 메서드
    void decrementGreatCount(Long postNo);

    // 게시글의 stupid_count 감소 메서드
    void decrementStupidCount(Long postNo);

    GreatAndStupidCount selectGreatAndStupidCount(Long postNo);

    // greatCount가 가장 높은 3개의 게시물 조회
    List<PostResponseDTO> getTop3PostsByGreatCount();
}
