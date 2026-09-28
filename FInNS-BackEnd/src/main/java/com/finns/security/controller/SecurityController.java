package com.finns.security.controller;

import lombok.extern.log4j.Log4j;
import com.finns.security.account.domain.CustomUser;
import com.finns.security.account.domain.MemberVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Log4j
@RequestMapping("/security")
@Controller
public class SecurityController {
    @GetMapping("/all") // 모두 접근 가능
    public void doAll() {
        log.info("do all can access everybody");
    }

    // most used
    @GetMapping("/member")
    public void doMember(Principal principal) {
        log.info("username = " + principal.getName()); // sangyeop0715
    }

    @GetMapping("/login")
    public void login() { // /security/login
        log.info("login page");
    }

    @GetMapping("/admin")
    public void doAdmin(@AuthenticationPrincipal CustomUser customUser) {
        MemberVO member = customUser.getMember();
        log.info("username = " + member);
    }

    @GetMapping("/logout")
    public void logout() {
        log.info("logout page");
    }

}
