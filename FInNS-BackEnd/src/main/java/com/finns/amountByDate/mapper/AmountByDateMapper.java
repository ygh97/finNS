package com.finns.amountByDate.mapper;

import com.finns.amountByDate.dto.AmountByDate;
import com.finns.post.dto.UpdateAmountDTO;

import java.util.List;

public interface AmountByDateMapper {
    // 특정 유저의 날짜별 금액 가져오기(달력 페이지 진입 시)
    List<AmountByDate> selectAllByUser(Long userNo);

    // 날짜와 유저로 금액 업데이트(갱신 시)
    void upsertByDateAndUser(UpdateAmountDTO updateAmountDTO);
}
