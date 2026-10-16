package com.finns.card.controller;

import com.finns.card.dto.Card;
import com.finns.card.service.CardService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "CardController", tags = "카드")
public class CardController {
    private final CardService cardService;

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

