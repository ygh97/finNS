package com.finns.greatOrStupid.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateGreatOrStupidRequestDTO {
    private Long userNo;
    private Long postNo;
    private boolean greatOrStupid;
}
