package com.multi.security.util;

import com.finns.security.util.JwtProcessor;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// DB·스프링 컨텍스트 없이 동작하는 단위 테스트
class JwtProcessorTest {
    private static final String SECRET = "test-secret-key-must-be-at-least-32-bytes-long!!";
    private final JwtProcessor jwtProcessor = new JwtProcessor(SECRET);

    @Test
    void 생성한_토큰에서_username을_꺼낸다() {
        String token = jwtProcessor.generateToken("user0");
        assertEquals("user0", jwtProcessor.getUsername(token));
    }

    @Test
    void 다른_키로_서명한_토큰은_거부한다() {
        JwtProcessor other = new JwtProcessor("another-secret-key-must-be-at-least-32-bytes!!");
        String token = other.generateToken("user0");
        assertThrows(JwtException.class, () -> jwtProcessor.getUsername(token));
    }
}
