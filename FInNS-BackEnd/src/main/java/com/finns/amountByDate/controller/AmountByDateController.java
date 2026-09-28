package com.finns.amountByDate.controller;

import com.finns.amountByDate.dto.AmountByDate;
import com.finns.amountByDate.service.AmountByDateService;
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
@Api(value = "AmountByDateController", tags = "날짜별 총 소비 정보")
@PropertySource({"classpath:/application.properties"})
@CrossOrigin(origins = "http://localhost:5173") // 클라이언트의 도메인을 허용
public class AmountByDateController {

    private final AmountByDateService amountByDateService;

    @GetMapping("/users/{no}/amountByDate")
    public ResponseEntity<List<AmountByDate>> getUserDailySpending(@PathVariable("no") Long userNo) {
        List<AmountByDate> dailySpendingByUser = amountByDateService.getAmountsForDateByUser(userNo);
        return ResponseEntity.ok(dailySpendingByUser);
    }


}
