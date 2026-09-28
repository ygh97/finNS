package com.finns.greatOrStupid.controller;

import com.finns.greatOrStupid.dto.UpdateGreatOrStupidRequestDTO;
import com.finns.greatOrStupid.service.GreatOrStupidService;
import com.finns.post.dto.GreatAndStupidCount;
import com.finns.security.account.domain.CustomUser;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(value = "GreatOrStupidController", tags = "좋아요 싫어요")
@PropertySource({"classpath:/application.properties"})
@RestController
@RequiredArgsConstructor
public class GreatOrStupidController {

    private final GreatOrStupidService greatOrStupidService;

    @PutMapping("/greatOrStupid")
    public ResponseEntity<GreatAndStupidCount> toggleGreat(@RequestBody UpdateGreatOrStupidRequestDTO updateGreatOrStupidRequestDTO,
                                                           @AuthenticationPrincipal CustomUser user) {
        updateGreatOrStupidRequestDTO.setUserNo((long) user.getMember().getUser_no());
        GreatAndStupidCount greatAndStupidCount = greatOrStupidService.toggleGreat(updateGreatOrStupidRequestDTO);
        return ResponseEntity.ok(greatAndStupidCount);
    }

    @GetMapping("/greatOrStupid/{postNo}/isGreat")
    public ResponseEntity<Boolean> isGreat(@PathVariable Long postNo, @AuthenticationPrincipal CustomUser user) {
        Boolean result = greatOrStupidService.isGreat((long) user.getMember().getUser_no(), postNo);
        return ResponseEntity.ok(result);
    }
}