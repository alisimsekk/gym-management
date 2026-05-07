package com.alisimsek.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.alisimsek.constant.AppConstants.BARER_WITH_BLANK;

@Slf4j
@Component
@RequiredArgsConstructor
class JwtLogoutHandler implements LogoutHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
        log.info("Logout request received");
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BARER_WITH_BLANK)) {
            String token = header.substring(7);
            jwtUtil.removeTokenFromActiveTokensList(token);
        }
        SecurityContextHolder.clearContext();
        log.info("Logout successful");
    }
}
