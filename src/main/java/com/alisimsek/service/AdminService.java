package com.alisimsek.service;

import com.alisimsek.dto.request.AdminCreateRequest;
import com.alisimsek.dto.response.UserRegistrationResponse;

public interface AdminService {
    UserRegistrationResponse createAdmin(AdminCreateRequest request);
}