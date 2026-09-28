package com.finns._config;

/**
 * Vue Router(history 모드) 화면 경로.
 * 이 경로로 직접 접속하거나 새로고침하면 서버가 index.html을 돌려주고, 화면 자체는 로그인 없이 열린다.
 * (데이터 API는 여전히 인증 필요 - 로그인 여부는 프론트 라우터 가드가 판단)
 * FInNS-FrontEnd/src/router/index.js 에 화면을 추가하면 여기에도 추가한다.
 */
public final class SpaRoutes {

    public static final String INDEX = "forward:/resources/index.html";

    public static final String[] PATHS = {
            "/",
            "/mbti",
            "/profile/**",
            "/deposit/**",
            "/installmentSavings/**",
            "/card/**",
            "/payDetails/**",
            "/postDetails/**",
            "/postView/**",
            "/cardworldcup/**",
            "/auth/**",
    };

    private SpaRoutes() {
    }
}
