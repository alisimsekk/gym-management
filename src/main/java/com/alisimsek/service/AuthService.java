package com.alisimsek.service;

import com.alisimsek.dto.request.ChangePasswordRequest;
import com.alisimsek.dto.request.LoginRequest;
import com.alisimsek.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse authenticateUser(LoginRequest loginRequest);

    void changePassword(ChangePasswordRequest request);

    void onFailure(String username);

    void resetUserFailLoginAttempt(String username);

}
