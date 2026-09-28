package com.finns.finance.service;

import com.finns.finance.dto.CardDTO;
import com.finns.finance.dto.Finance;
import com.finns.finance.dto.FinanceCount;
import com.finns.finance.dto.FinanceDTO;
import com.finns.finance.mapper.FinanceMapper;
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
public class FinanceService {
    private final FinanceMapper financeMapper;

    // 예금 상품 리스트 반환
    public List<FinanceDTO> getDepositList() {
        return financeMapper.getDepositProducts();
    }

    // 적금 상품 리스트 반환
    public List<FinanceDTO> getinstallList() {
        return financeMapper.getinstallProducts();
    }

    // 금융상품 한개 검색
    public FinanceDTO getinstallList(Long no) {return financeMapper.selectOneProduct(no);}

    // 금융상품 로고 url 검색

    //가장 높은 최고금리 예금
    public FinanceDTO getHighestIntrRateForDeposit() {
        return financeMapper.selectHighestIntrRateForDeposit();
    }
    //가장 높은 최고금리 적금
    public FinanceDTO getHighestIntrRateForSavings() {
        return financeMapper.selectHighestIntrRateForSavings();
    }

    public FinanceCount getTopDepositProductByUsers() {
        return financeMapper.selectTopDepositProductByUsers();
    }

    public FinanceCount getTopSavingsProductByUsers() {
        return financeMapper.selectTopSavingsProductByUsers();
    }
    // 카드 상품 리스트 반환
    public List<CardDTO> getCardList() {return financeMapper.getCardProducts();}

    // 카드 상품 한개 검색
    public CardDTO getCardList(Long no) {return financeMapper.selectOneCard(no);}

    public List<FinanceDTO> getProductsByUser(Long userNo) {
        return Optional.of(financeMapper.selectProductsByUser(userNo))
                .orElseThrow(NoSuchElementException::new);
    }
}
