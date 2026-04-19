package com.alisimsek.service;

import com.alisimsek.model.User;

import java.util.Optional;

public interface UserService {

    Long countUserByUsername(String username);

    Optional<User> findByUsername(String username);

    void changeUserStatus(Long id);

    User getUserByUsername(String username);
}
