package com.busanit501.team_project.config;

import com.busanit501.team_project.security.APIUserDetailsService;
import com.busanit501.team_project.security.filter.APILoginFilter;
import com.busanit501.team_project.security.filter.RefreshTokenFilter;
import com.busanit501.team_project.security.filter.TokenCheckFilter;
import com.busanit501.team_project.security.handler.APILoginSuccessHandler;
import com.busanit501.team_project.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// 작업 순서1
@Log4j2
@Configuration

@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {
    //추가 1-1
    private final APIUserDetailsService apiUserDetailsService;
    private final JWTUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("시큐리티 동작 확인 ====webSecurityCustomizer======================");
        return (web) ->
                web.ignoring()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("===========config=================");


        // 인증 관련된 설정을 하는 도구.
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                // 우리 코드에서 로그인을 담당하는 도구 옵션 추가.
                .userDetailsService(apiUserDetailsService)
                // 평문 암호화 해주는 도구 옵션 추가.
                .passwordEncoder(passwordEncoder());

        // Get AuthenticationManager 세팅1
        AuthenticationManager authenticationManager =
                authenticationManagerBuilder.build();

        //반드시 필요 세팅1
        // 적용하기.
        http.authenticationManager(authenticationManager);

        //APILoginFilter 세팅1
        // 아이디:mid- lsy, 패스워드: mpw- 1234 첨부해서,
        // localhost:8080/generateToken
        // 디비 등록된 유저에 대해서만, 토큰 발급.

        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken");
        apiLoginFilter.setAuthenticationManager(authenticationManager);

        // APILoginSuccessHandler 생성: 인증 성공 후 처리 로직을 담당
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);


// SuccessHandler 설정: 로그인 성공 시 APILoginSuccessHandler가 호출되도록 설정
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);

        //APILoginFilter의 위치 조정 세팅1, 사용자 인증 전에 ,
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        // /api 경로에 대해 TokenCheckFilter 적용
        http.addFilterBefore(
                tokenCheckFilter(jwtUtil,apiUserDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        // RefreshTokenFilter를 TokenCheckFilter 이전에 등록
        http.addFilterBefore(
                new RefreshTokenFilter("/refreshToken", jwtUtil),
                TokenCheckFilter.class
        );
        //cors 정책 설정
        http.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
        );
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, APIUserDetailsService apiUserDetailsService){
        return new TokenCheckFilter(apiUserDetailsService, jwtUtil);
    }
//    @Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));

        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
