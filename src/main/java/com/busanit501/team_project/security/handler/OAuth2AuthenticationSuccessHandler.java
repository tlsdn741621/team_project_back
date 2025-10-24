package com.busanit501.team_project.security.handler;

import com.busanit501.team_project.dto.MemberDTO;
import com.busanit501.team_project.service.MemberService;
import com.busanit501.team_project.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final MemberService memberService;

    // 🌟 1. ServletException 제거
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 Login Success! Authentication: {}", authentication);

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 소셜 계정 정보 추출
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String socialId = oAuth2User.getName();
        String email = null; // 초기화
        String nickname = null;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null) {
                // 🌟 2. 카카오 닉네임 추출 로직
                Object profileObj = kakaoAccount.get("profile");
                if (profileObj instanceof Map) {
                    Map<String, Object> profile = (Map<String, Object>) profileObj;
                    nickname = (String) profile.get("nickname");
                }
                email = (String) kakaoAccount.get("email");
            }
        } else if ("google".equals(registrationId)) {
            nickname = oAuth2User.getAttribute("name");
            email = oAuth2User.getAttribute("email");
        }

        log.info("Social Login Info - Id: {}, Email: {}, Nickname: {}", socialId, email, nickname);

        // 2. 우리 앱의 회원 정보로 처리 (회원가입 또는 로그인)
        // 🔴 [수정] 통합 메서드 processSocialLogin 호출
        // 이 메서드 내부에서 DB 조회 및 저장이 모두 처리됩니다.
        memberService.processSocialLogin(registrationId, socialId, email, nickname);

        // 3. 우리 앱의 JWT 토큰 발행
        String memberId = registrationId + "_" + socialId; // JWT 발행을 위해 memberId 다시 생성
        Map<String, Object> claims = Map.of("mid", memberId);
        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 30);

        // 4. React 프론트엔드로 JWT 토큰 전달 (리디렉션)
        String redirectUrl = String.format("http://localhost:5173/social-login-success?accessToken=%s&refreshToken=%s", accessToken, refreshToken);
        response.sendRedirect(redirectUrl);
    }
}
