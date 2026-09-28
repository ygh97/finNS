package com.finns.greatOrStupid.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateGreatOrStupidResponseDTO {
    private Long greatCount;
    private Long stupidCount;
}
