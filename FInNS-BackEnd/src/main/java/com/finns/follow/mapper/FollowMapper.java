package com.finns.follow.mapper;

import com.finns.follow.dto.FollowDTO;
import com.finns.security.account.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {

    /**
     * 팔로우 관계를 추가한다.
     * @param followDTO 팔로우 정보를 담은 DTO
     */
    void insertFollow(FollowDTO followDTO);

    /**
     * 팔로우 관계를 삭제한다 (언팔로우).
     * @param followDTO 팔로우 정보를 담은 DTO
     */
    void deleteFollow(FollowDTO followDTO);

    /**
     * 특정 사용자가 팔로우하고 있는 사용자 목록을 가져온다.
     * @param user_no 사용자 번호
     * @param viewer_no 목록을 보는 사용자 번호 (following 여부 계산용)
     * @return 팔로잉 사용자 목록
     */
    List<MemberVO> selectFollowingByUserNo(@Param("user_no") int user_no, @Param("viewer_no") int viewer_no);

    /**
     * 특정 사용자를 팔로우하는 사용자 목록을 가져온다.
     * @param user_no 사용자 번호
     * @param viewer_no 목록을 보는 사용자 번호 (following 여부 계산용)
     * @return 팔로워 사용자 목록
     */
    List<MemberVO> selectFollowerByUserNo(@Param("user_no") int user_no, @Param("viewer_no") int viewer_no);

    /**
     * 팔로우 관계가 이미 존재하는지 확인한다.
     * @param user_no 팔로우 정보를 담은 DTO
     * @return 팔로우 관계가 존재하면 1, 존재하지 않으면 0
     */
    int checkFollowExists(@Param("user_no") long user_no, @Param("to_user_no") long to_user_no);

    int countFollowers(int user_no);
    int countFollowing(int user_no);
}