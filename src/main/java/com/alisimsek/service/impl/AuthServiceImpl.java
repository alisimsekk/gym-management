package com.alisimsek.service.impl;

import com.alisimsek.dto.request.ChangePasswordRequest;
import com.alisimsek.dto.request.LoginRequest;
import com.alisimsek.dto.response.AuthResponse;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.exception.customException.PasswordMismatchException;
import com.alisimsek.exception.customException.UserMismatchException;
import com.alisimsek.exception.customException.UserLockedException;
import com.alisimsek.model.User;
import com.alisimsek.model.records.LoginAttempt;
import com.alisimsek.repository.UserRepository;
import com.alisimsek.security.CustomUserDetailsService;
import com.alisimsek.security.JwtUtil;
import com.alisimsek.service.AuthService;
import com.alisimsek.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${login.max-attempts}")
    private int MAX_ATTEMPTS;

    @Value("${login.block-time-minutes}")
    private int BLOCK_TIME_MINUTES;

    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    private final UserService userService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        ensureUserNotBlocked(loginRequest.username());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.username());

        return jwtUtil.getAuthResponse(userDetails);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String username = request.username();

        log.info("Changing password for user {}", username);
        User user = userService.findByUsername(username)
                .filter(User::isActive)
                .orElseThrow(() -> new EntityNotFoundException("User"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String loggedInUsername = authentication.getName();

        if (!username.equals(loggedInUsername)) {
            throw new UserMismatchException();
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new PasswordMismatchException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user {}", username);
    }

    @Override
    public void onFailure(String username) {
        LoginAttempt loginAttempt = loginAttempts.getOrDefault(username, new LoginAttempt(0, null));
        int next = loginAttempt.count() + 1;
        LocalDateTime blockedUntil = loginAttempt.blockedUntil();
        if (next >= MAX_ATTEMPTS) {
            blockedUntil = LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES);
            User user = userService.getUserByUsername(username);
            user.setLocked(true);
            userRepository.save(user);
            log.warn("User {} reached max login attempts. User has locked until {}", username, blockedUntil);
        }
        loginAttempts.put(username, new LoginAttempt(next, blockedUntil));
    }

    @Override
    public void resetUserFailLoginAttempt(String username) {
        loginAttempts.remove(username);
    }

    private void ensureUserNotBlocked(String username) {
        LoginAttempt loginAttempt = loginAttempts.get(username);
        if (loginAttempt != null && loginAttempt.blockedUntil() != null && LocalDateTime.now().isBefore(loginAttempt.blockedUntil())) {
            log.warn("User {} is currently blocked until {}", username, loginAttempt.blockedUntil());
            throw new UserLockedException();
        }
        User user = userService.getUserByUsername(username);
        if (user.isLocked()) {
            log.info("User {} was locked in DB but block time has expired. Unlocking user.", username);
            loginAttempts.remove(username);
            user.setLocked(false);
            userRepository.save(user);
        } else {
            log.info("User {} is not blocked. Proceeding with authentication.", username);
        }
    }
}
