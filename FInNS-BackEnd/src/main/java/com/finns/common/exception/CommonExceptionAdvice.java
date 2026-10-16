package com.finns.common.exception;

import com.finns._config.SpaRoutes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class CommonExceptionAdvice {
    // 없는 경로는 SPA로 넘겨 프론트 라우터가 처리하게 한다
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex) {
        log.warn(ex.getMessage());
        return SpaRoutes.INDEX;
    }
}
