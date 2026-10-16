package com.finns.security.config;

import com.finns._config.SpaRoutes;
import com.finns.security.filter.AuthenticationErrorFilter;
import com.finns.security.filter.JwtAuthenticationFilter;
import com.finns.security.filter.JwtUsernamePasswordAuthenticationFilter;
import com.finns.security.handler.CustomAccessDeniedHandler;
import com.finns.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWT 기반 무상태 인증. 화면(index.html)·정적 파일·회원가입·로그인만 공개하고 나머지 API는 인증이 필요하다.
 * 프론트와 API가 같은 출처에서 서빙되므로 CORS 설정은 두지 않는다 (개발 중에는 Vite 프록시 사용).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationErrorFilter authenticationErrorFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    // 로그인 필터는 이 설정이 만드는 AuthenticationManager가 필요해서 생성자가 아닌 필드로 주입한다
    @Autowired
    private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic().disable()
                .csrf().disable() // 쿠키 세션을 쓰지 않고 Authorization 헤더로만 인증
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        // 로그인(POST /api/auth/login)은 로그인 필터가 먼저 처리한다
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, SpaRoutes.PATHS).permitAll()
                .antMatchers(HttpMethod.POST, "/api/member").permitAll() // 회원가입
                .antMatchers(HttpMethod.GET, "/api/member/checkusername/**").permitAll()
                .antMatchers("/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 정적 파일과 업로드 이미지는 보안 필터를 거치지 않는다
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**", "/resources/**", "/upload/**");
    }
}
