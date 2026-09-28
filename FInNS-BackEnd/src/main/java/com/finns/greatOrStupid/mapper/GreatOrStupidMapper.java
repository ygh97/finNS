package com.finns.greatOrStupid.mapper;

import com.finns.greatOrStupid.dto.UpdateGreatOrStupidRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GreatOrStupidMapper {
    // 특정 사용자와 게시물에 해당하는 isGreat 값을 조회

    // 사용자와 게시물에 따른 isGreat 값을 삽입
    void insert(UpdateGreatOrStupidRequestDTO updateGreatOrStupidRequestDTO);

    // 사용자와 게시물에 따른 isGreat 값을 업데이트
    void updateIsGreat(@Param("userNo") Long userNo, @Param("postNo") Long postNo, @Param("isGreat") Boolean isGreat);

    // 사용자와 게시물에 따른 isGreat 값을 삭제
    void deleteByUserNoAndPostNo(@Param("userNo") Long userNo, @Param("postNo") Long postNo);

    // 사용자 번호와 게시물 번호에 따른 isGreat 값을 조회하는 메서드
    Boolean findByUserNoAndPostNo(@Param("userNo") Long userNo, @Param("postNo") Long postNo);
}
