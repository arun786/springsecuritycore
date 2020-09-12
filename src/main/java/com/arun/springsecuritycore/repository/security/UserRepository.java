package com.arun.springsecuritycore.repository.security;

import com.arun.springsecuritycore.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author arun on 9/11/20
 */
public interface UserRepository extends JpaRepository<UserDomain, Integer> {

    Optional<UserDomain> findByUsername(String userName);
}
