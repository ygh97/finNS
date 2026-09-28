package com.finns.common.exception;

import com.finns.member.exception.PasswordMissmatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class RestExceptionAdvice {
    @ExceptionHandler(PasswordMissmatchException.class)
    public ResponseEntity<?> handlePasswordError(Exception ex) {
        return error(400, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(Exception ex) {
        return error(403, "권한이 없습니다.");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(Exception ex) {
        return error(404, ex.getMessage() != null ? ex.getMessage() : "데이터를 찾을 수 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleError(Exception ex) {
        log.error(ex.getMessage(), ex);
        return error(500, "서버 오류가 발생했습니다.");
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8")
                .body(message);
    }

}
