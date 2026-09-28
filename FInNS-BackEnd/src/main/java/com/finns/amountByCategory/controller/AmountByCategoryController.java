package com.finns.amountByCategory.controller;

import com.finns.amountByCategory.dto.AmountByCategory;
import com.finns.amountByCategory.service.AmountByCategoryService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "AmountByCategoryController", tags = "카테고리별 총 소비 정보")
@PropertySource({"classpath:/application.properties"})
@CrossOrigin(origins = "http://localhost:5173") // 클라이언트의 도메인을 허용
public class AmountByCategoryController {

    private final AmountByCategoryService amountByCategoryService;

    @GetMapping("/users/{no}/amountByCategory")
    public ResponseEntity<List<AmountByCategory>> getUserCategorySpending(@PathVariable("no") Long userNo) {
        List<AmountByCategory> categorySpendingByUser = amountByCategoryService.getAmountsForCategoryByUser(userNo);
        return ResponseEntity.ok(categorySpendingByUser);
    }


}
