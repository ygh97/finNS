package com.finns.user.service;

import com.finns.Mbti;
import com.finns.amountByCategory.service.AmountByCategoryService;
import com.finns.user.dto.*;
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
public class UserService {

    private final UserMapper userMapper;
    private final AmountByCategoryService amountByCategoryService;

    public User getUser(Long userNo) {
        return Optional.ofNullable(userMapper.selectOne(userNo))
                .orElseThrow(NoSuchElementException::new);
    }

    // 팔로우 여부(follow)는 쿼리에서 함께 계산
    public List<SearchUserDTO> getUsers(Long userNo) {
        return userMapper.selectAll(userNo);
    }

    public List<UserTop3DTO> getTop3ForAmountByDate(YearAndMonthDTO yearAndMonthDTO) {
        return userMapper.selectTop3ForAmountByDate(yearAndMonthDTO);
    }

    public List<UserRecommendResponseDTO> getRecommend5ByMbti(UserRecommendRequestDTO userRecommendRequestDTO) {
        return userMapper.selectRecommend5ByMbti(userRecommendRequestDTO);
    }

    @Transactional
    public void setMbtiByCategory(Long userNo) {
        String topCategory = amountByCategoryService.calculateTopCategory(userNo);
        if (topCategory == null) {
            throw new NoSuchElementException("MBTI를 분석할 소비 내역이 없습니다.");
        }
        String mbtiName = Mbti.getMbtiNameByCategory(topCategory);
        SetMbtiDTO setMbtiDTO = new SetMbtiDTO(userNo, mbtiName);
        userMapper.updateMbti(setMbtiDTO);
    }
}
