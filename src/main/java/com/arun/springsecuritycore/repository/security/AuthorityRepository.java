package com.arun.springsecuritycore.repository.security;

import com.arun.springsecuritycore.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author arun on 9/11/20
 */
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
