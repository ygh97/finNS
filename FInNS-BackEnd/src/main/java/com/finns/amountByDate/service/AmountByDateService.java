package com.finns.amountByDate.service;

import com.finns.amountByDate.dto.AmountByDate;
import com.finns.amountByDate.mapper.AmountByDateMapper;
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
public class AmountByDateService {

    private final AmountByDateMapper mapper;

    public List<AmountByDate> getAmountsForDateByUser(Long userNo) {
        return Optional.ofNullable(mapper.selectAllByUser(userNo))
                .orElseThrow(NoSuchElementException::new);
    }

}
