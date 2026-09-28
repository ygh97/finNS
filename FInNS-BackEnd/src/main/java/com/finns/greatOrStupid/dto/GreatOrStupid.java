package com.finns.greatOrStupid.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GreatOrStupid {
    private Long userNo;
    private Long postNo;
    private boolean isGreat;
}
