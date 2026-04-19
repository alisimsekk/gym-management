package com.alisimsek.service;

import com.alisimsek.exception.ExceptionMessage;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.User;
import com.alisimsek.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Long countUserByUsername(String username) {
        return userRepository.countByUsernameStartingWith(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void changeUserStatus(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error(ExceptionMessage.getEntityNotFoundMessage("User", userId));
            return;
        }

        User user = userOptional.get();
        boolean isActive = user.isActive();

        user.setActive(!user.isActive());
        userRepository.save(userOptional.get());

        log.info("User with id: {} status changed from {} to {}", userId, isActive, user.isActive());
    }

    @Override
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User"));
    }
}
