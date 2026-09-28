package com.finns.finance.controller;

import com.finns.finance.dto.CardDTO;
import com.finns.finance.dto.Finance;
import com.finns.finance.dto.FinanceCount;
import com.finns.finance.dto.FinanceDTO;
import com.finns.finance.service.FinanceService;
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
@RequestMapping("/product")
@Api(value = "FinanceController", tags = "금융상품 정보")
@CrossOrigin(origins = "http://localhost:5173") // 클라이언트의 도메인을 허용
public class FinanceController {

    private final FinanceService financeService;

    // 금융 상품 리스트를 반환하는 API
    @GetMapping("/{financeProductType}")
    public ResponseEntity<List<FinanceDTO>> getProductList(@PathVariable("financeProductType") String financeProductType) {
        if ("01".equals(financeProductType)) {
            return ResponseEntity.ok(financeService.getDepositList());
        } else if ("02".equals(financeProductType)) {
            return ResponseEntity.ok(financeService.getinstallList());
        } else {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }
    // 특정 금융 상품 조회 API
    @GetMapping("/no/{financeProductNo}")
    public FinanceDTO getFinanceProduct(@PathVariable("financeProductNo") Long financeProductNo) {
        return financeService.getinstallList(financeProductNo);
    }

    // 예금(01)의 가장 높은 금리를 가진 항목 반환
    @GetMapping("/highest/deposit")
    public FinanceDTO getHighestIntrRateForDeposit() {
        return financeService.getHighestIntrRateForDeposit();
    }

    // 적금(02)의 가장 높은 금리를 가진 항목 반환
    @GetMapping("/highest/savings")
    public FinanceDTO getHighestIntrRateForSavings() {
        return financeService.getHighestIntrRateForSavings();
    }

    // 사용자가 가장 많은 예금(01)
    @GetMapping("/top/deposit")
    public FinanceCount getTopDepositByUsers() {
        return financeService.getTopDepositProductByUsers();
    }
    //사용자가 가장 많은 적금(01)
    @GetMapping("/top/savings")
    public FinanceCount getTopSavingsByUsers() {
        return financeService.getTopSavingsProductByUsers();
    }

    // 카드 리스트를 반환하는 API
    @GetMapping("/card")
    public ResponseEntity<List<CardDTO>> getCardList() {
        List<CardDTO> cardList = financeService.getCardList();  // 카드 리스트 조회
        return ResponseEntity.ok(cardList);
    }

    // 카드 리스트를 반환하는 API
    @GetMapping("/card/{cardNo}")
    public CardDTO getCardProduct(@PathVariable("cardNo") Long cardNo) {
        return financeService.getCardList(cardNo);
    }

    @GetMapping("/users/{no}/products")
    public ResponseEntity<List<FinanceDTO>> getEnrolledProductsByUser(@PathVariable("no") Long userNo) {
        List<FinanceDTO> enrolledProducts = financeService.getProductsByUser(userNo);
        return ResponseEntity.ok(enrolledProducts);
    }
}
