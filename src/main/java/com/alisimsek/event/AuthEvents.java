package com.alisimsek.event;

import com.alisimsek.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AuthEvents implements ApplicationListener<AbstractAuthenticationEvent> {

    private final AuthService authService;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailureBadCredentialsEvent f) {
            String username = (String) f.getAuthentication().getPrincipal();
            authService.onFailure(username);
        } else if (event instanceof AuthenticationSuccessEvent s) {
            String username = s.getAuthentication().getName();
            authService.resetUserFailLoginAttempt(username);
        }
    }
}
