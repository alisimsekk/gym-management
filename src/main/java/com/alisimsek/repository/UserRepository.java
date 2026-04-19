package com.alisimsek.repository;

import com.alisimsek.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Long countByUsernameStartingWith(String username);

    Optional<User> findByUsername(String username);
}
