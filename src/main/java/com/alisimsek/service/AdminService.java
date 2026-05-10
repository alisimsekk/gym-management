package com.alisimsek.service;

import com.alisimsek.dto.request.AdminCreateRequest;
import com.alisimsek.dto.request.UpdateAdminProfileRequest;
import com.alisimsek.dto.response.AdminProfileResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;

public interface AdminService {
    UserRegistrationResponse createAdmin(AdminCreateRequest request);

    AdminProfileResponse getMyAdminProfile(String username);

    AdminProfileResponse updateMyAdminProfile(String username, UpdateAdminProfileRequest request);
}