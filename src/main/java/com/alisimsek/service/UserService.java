package com.alisimsek.service;

import com.alisimsek.dto.request.UserSearchRequest;
import com.alisimsek.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Long countUserByUsername(String username);

    Optional<User> findByUsername(String username);

    void changeUserStatus(Long id);

    User getUserByUsername(String username);

    List<User> searchUsers(UserSearchRequest searchRequest);
}
