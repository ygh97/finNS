package com.finns.user.mapper;

import com.finns.post.dto.ChangeRenewStatusDTO;
import com.finns.user.dto.*;

import java.util.List;

public interface UserMapper {
    User selectOne(Long userNo);
    List<SearchUserDTO> selectAll(Long userNo);
    List<UserTop3DTO> selectTop3ForAmountByDate(YearAndMonthDTO yearAndMonthDTO);
    List<UserRecommendResponseDTO> selectRecommend5ByMbti(UserRecommendRequestDTO userRecommendRequestDTO);
    void updateRenewTime(ChangeRenewStatusDTO changeRenewStatusDTO);
    void updateMbti(SetMbtiDTO setMbtiDTO);
}
