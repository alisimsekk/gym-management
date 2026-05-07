package com.alisimsek.service.impl;

import com.alisimsek.dto.request.AdminCreateRequest;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.enums.UserType;
import com.alisimsek.model.Admin;
import com.alisimsek.repository.AdminRepository;
import com.alisimsek.service.AdminService;
import com.alisimsek.service.UserService;
import com.alisimsek.util.PasswordGenerator;
import com.alisimsek.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordGenerator passwordGenerator;
    private final UserService userService;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse createAdmin(AdminCreateRequest request) {
        log.info("Creating new admin user.");

        String rawPassword = passwordGenerator.generatePassword();
        Admin admin = buildAdmin(request, rawPassword);

        adminRepository.save(admin);

        return new UserRegistrationResponse(admin.getUsername(), rawPassword);
    }

    private Admin buildAdmin(AdminCreateRequest createRequest, String rawPassword) {
        String username = getUsername(createRequest.firstName(), createRequest.lastName());
        Admin admin = new Admin();
        admin.setFirstName(createRequest.firstName());
        admin.setLastName(createRequest.lastName());
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setUserType(UserType.ADMIN);
        return admin;
    }

    private String getUsername(String firstName, String lastName) {
        log.info("Generating username for firstName: {} and lastName: {}", firstName, lastName);
        String username = firstName.concat(".").concat(lastName).toLowerCase();

        Long totalUserCountWithSameUsername = userService.countUserByUsername(username);

        if (totalUserCountWithSameUsername >= 1) {
            username = usernameGenerator.generateUsername(username, totalUserCountWithSameUsername);
        }
        return username;
    }
}