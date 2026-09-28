package com.finns.card.controller;

import com.finns.card.dto.Card;
import com.finns.card.pagination.PageResponse;
import com.finns.card.service.CardService;
import com.finns.finance.dto.FinanceDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "CardController", tags = "카드")
@PropertySource({"classpath:/application.properties"})
@CrossOrigin(origins = "http://localhost:5173") // 클라이언트의 도메인을 허용
public class CardController {
    private final CardService cardService;

    // 카드 목록 조회
    @GetMapping("/cards")
    public PageResponse<Card> getCards(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return cardService.getCards(page, size);
    }

    // 특정 카드 상세 조회
    @GetMapping("/cards/{no}")
    public Card getCard(@PathVariable("no") long cardNo) {
        return cardService.getCardById(cardNo);
    }

    @GetMapping("/users/{no}/recommendCards/{num}")
    public ResponseEntity<List<Card>> recommendCardsByUser(@PathVariable("no") long userNo, @PathVariable("num") int num) {
        List<Card> recommend3Cards = cardService.getRecommendNCards(userNo, num);
        return ResponseEntity.ok(recommend3Cards);
    }

    @GetMapping("/users/{no}/cards")
    public ResponseEntity<List<Card>> getEnrolledCardsByUser(@PathVariable("no") Long userNo) {
        List<Card> enrolledCards = cardService.getCardsByUser(userNo);
        return ResponseEntity.ok(enrolledCards);
    }
}

