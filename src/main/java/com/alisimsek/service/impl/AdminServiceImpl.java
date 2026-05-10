package com.alisimsek.service.impl;

import com.alisimsek.dto.request.AdminCreateRequest;
import com.alisimsek.dto.request.UpdateAdminProfileRequest;
import com.alisimsek.dto.response.AdminProfileResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.enums.UserType;
import com.alisimsek.exception.customException.EntityAlreadyExistsException;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.Admin;
import com.alisimsek.model.User;
import com.alisimsek.repository.AdminRepository;
import com.alisimsek.repository.UserRepository;
import com.alisimsek.service.AdminService;
import com.alisimsek.service.UserService;
import com.alisimsek.util.PasswordGenerator;
import com.alisimsek.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordGenerator passwordGenerator;
    private final UserService userService;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse createAdmin(AdminCreateRequest request) {
        log.info("Creating new admin user.");

        String email = request.email().trim();
        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException();
        }

        String rawPassword = passwordGenerator.generatePassword();
        Admin admin = buildAdmin(request, email, rawPassword);

        adminRepository.save(admin);

        return new UserRegistrationResponse(admin.getUsername(), rawPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProfileResponse getMyAdminProfile(String username) {
        User user = requireAdminUser(username);
        return toAdminProfileResponse(user);
    }

    @Override
    @Transactional
    public AdminProfileResponse updateMyAdminProfile(String username, UpdateAdminProfileRequest request) {
        User user = requireAdminUser(username);
        String email = request.email().trim();
        if (userRepository.existsByEmailAndIdNot(email, user.getId())) {
            throw new EntityAlreadyExistsException();
        }
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        userRepository.save(user);
        return toAdminProfileResponse(user);
    }

    private User requireAdminUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName()));
        if (!UserType.ADMIN.equals(user.getUserType())) {
            throw new AccessDeniedException("Not an admin account");
        }
        return user;
    }

    private AdminProfileResponse toAdminProfileResponse(User user) {
        return AdminProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isActive(user.isActive())
                .build();
    }

    private Admin buildAdmin(AdminCreateRequest createRequest, String email, String rawPassword) {
        String username = getUsername(createRequest.firstName(), createRequest.lastName());
        Admin admin = new Admin();
        admin.setFirstName(createRequest.firstName());
        admin.setLastName(createRequest.lastName());
        admin.setEmail(email);
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setUserType(UserType.ADMIN);
        admin.setActive(true);
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
