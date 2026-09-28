package com.finns.amountByCategory.mapper;

import com.finns.amountByCategory.dto.AmountByCategory;
import com.finns.post.dto.UpdateAmountDTO;

import java.util.List;

public interface AmountByCategoryMapper {
    // 특정 유저의 카테고리별 금액 가져오기(분석 페이지 진입 시)
    List<AmountByCategory> selectAllByUser(Long userNo);

    // 카테고리와 유저로 금액 업데이트(갱신 시)
    void upsertByCategoryAndUser(UpdateAmountDTO updateAmountDTO);

}
