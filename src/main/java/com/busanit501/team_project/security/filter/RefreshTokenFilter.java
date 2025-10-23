package com.busanit501.team_project.security.filter;

import com.busanit501.team_project.security.exception.RefreshTokenException;
import com.busanit501.team_project.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

// 작업 순서19
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {

        String path = request.getRequestURI();

        if (!path.equals(refreshPath)) {
            log.info("Skipping refresh token filter...");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Refresh Token Filter triggered for path: {}", path);

        Map<String, String> tokens = parseRequestJSON(request);
        if (tokens == null || !tokens.containsKey("accessToken") || !tokens.containsKey("refreshToken")) {
            log.error("Missing tokens in request.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing accessToken or refreshToken.");
            return;
        }

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        Map<String, Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info("Refresh Token Claims: {}", refreshClaims);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        Integer exp = (Integer)refreshClaims.get("exp");

        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

        Date current = new Date(System.currentTimeMillis());

        long gapTime = (expTime.getTime() - current.getTime());

        log.info("-----------------------------------------");
        log.info("current: " + current);
        log.info("expTime: " + expTime);
        log.info("gap: " + gapTime );

        String username = (String)refreshClaims.get("mid");
        log.info("username: " + username);
        String accessTokenValue = jwtUtil.generateToken(Map.of("username", username), 1);

        String refreshTokenValue = tokens.get("refreshToken");

        if(gapTime < (1000 * 60 * 60 * 24 * 3  ) ){
            log.info("new Refresh Token required...  ");
            refreshTokenValue = jwtUtil.generateToken(Map.of("username", username), 3);
        }

        log.info("Refresh Token result....................");
        log.info("accessToken: " + accessTokenValue);
        log.info("refreshToken: " + refreshTokenValue);

        sendTokens(accessTokenValue, refreshTokenValue, response);



    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error("Error reading JSON from request: {}", e.getMessage());
        }
        return null;
    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has expired.");
        } catch (Exception exception) {
            log.error("Access Token validation failed: {}", exception.getMessage());
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {
            return jwtUtil.validateToken(refreshToken);

        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException: Refresh Token has expired.");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);

        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException: Invalid Refresh Token format.");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.BAD_REFRESH);

        } catch (Exception exception) {
            log.error("Unexpected exception during Refresh Token validation: {}", exception.getMessage());
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of(
                "accessToken", accessTokenValue,
                "refreshToken", refreshTokenValue
        ));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send tokens to client", e);
        }
    }
}
