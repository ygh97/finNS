package com.finns.card.mapper;

import com.finns.card.dto.Card;
import com.finns.card.dto.RecommendNCardRequestDTO;
import com.finns.finance.dto.FinanceDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CardMapper {
    // 카드 목록 조회 (페이지네이션 포함)
    List<Card> selectAllCards(@Param("offset") int offset, @Param("limit") int limit);

    long countCards(); // 전체 카드 개수 반환

    Card selectCardById(long card_no); // 특정 카드 조회

    List<Card> selectRecommendNCards(RecommendNCardRequestDTO recommendNCardRequestDTO);

    List<Card> selectCardsByUser(Long userNo);

}
