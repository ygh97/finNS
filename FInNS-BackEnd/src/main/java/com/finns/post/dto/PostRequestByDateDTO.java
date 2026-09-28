package com.finns.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// requestDTO인 경우에는, 기본 생성자가 없으면 Jackson이 JSON 데이터를 PostRequestDTO 객체로 변환할 수 없기 때문에 필수로 추가
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostRequestByDateDTO {
	private Long userNo;
	private LocalDate date;
	private Boolean isOnlyPublic;
}
