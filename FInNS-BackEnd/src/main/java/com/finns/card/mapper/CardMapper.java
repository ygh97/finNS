package com.finns.card.mapper;

import com.finns.card.dto.Card;
import com.finns.card.dto.RecommendNCardRequestDTO;

import java.util.List;

public interface CardMapper {
    List<Card> selectRecommendNCards(RecommendNCardRequestDTO recommendNCardRequestDTO);

    List<Card> selectCardsByUser(Long userNo);

}
