package com.arun.springsecuritycore.repository.security;

import com.arun.springsecuritycore.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author arun on 9/11/20
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String userName);
}
