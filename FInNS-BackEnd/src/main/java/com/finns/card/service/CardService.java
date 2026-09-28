package com.finns.card.service;

import com.finns.amountByCategory.service.AmountByCategoryService;
import com.finns.card.dto.Card;
import com.finns.card.dto.RecommendNCardRequestDTO;
import com.finns.card.mapper.CardMapper;
import com.finns.card.pagination.PageResponse;
import com.finns.finance.dto.CardDTO;
import com.finns.finance.dto.FinanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource({"classpath:/application.properties"})
@Transactional(readOnly = true)
public class CardService {

    private final CardMapper cardMapper;
    private final AmountByCategoryService amountByCategoryService;

    public PageResponse<Card> getCards(int page, int size) {
        int offset = (page - 1) * size; // 페이지 수에 따라 오프셋 계산
        List<Card> cards = cardMapper.selectAllCards(offset, size);

        long totalElements = cardMapper.countCards(); // 총 카드 수를 가져옴
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(cards, totalPages, totalElements, size, page);
    }

    public Card getCardById(long card_no) {
        Card card = cardMapper.selectCardById(card_no);
        return card;
    }

    public List<Card> getRecommendNCards(Long userNo, int num) {
        String topCategoryByUser = amountByCategoryService.calculateTopCategory(userNo);
        if (topCategoryByUser == null) { // 분석할 소비 내역 없음
            return Collections.emptyList();
        }
        String cardCategory = matchingCategory(topCategoryByUser);
        RecommendNCardRequestDTO recommendNCardRequestDTO = new RecommendNCardRequestDTO(cardCategory, num);

        List<Card> recommendCards = cardMapper.selectRecommendNCards(recommendNCardRequestDTO);
        return recommendCards;
    }

    private String matchingCategory(String categoryByUser) {
        //1. 식비 · 카페 -> 식비
        //2. 쇼핑 -> 여가
        //3. 미용 -> 여가
        //4. 의료
        //5. 통신
        //6. 교통
        //7. 문화 · 여행 -> 여가
        //8. 교육
        //9. 술 · 유흥 -> 식비

        Map<String, String> CATEGORY_MAP = new HashMap<>();
        CATEGORY_MAP.put("식비 · 카페", "식비");
        CATEGORY_MAP.put("술 · 유흥", "식비");
        CATEGORY_MAP.put("쇼핑", "여가");
        CATEGORY_MAP.put("미용", "여가");
        CATEGORY_MAP.put("문화 · 여행", "여가");

        String cardCategory = CATEGORY_MAP.getOrDefault(categoryByUser, categoryByUser);
        return cardCategory;
    }

    public List<Card> getCardsByUser(Long userNo) {
        return Optional.of(cardMapper.selectCardsByUser(userNo))
                .orElseThrow(NoSuchElementException::new);
    }

}
