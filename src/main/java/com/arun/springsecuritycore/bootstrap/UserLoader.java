package com.arun.springsecuritycore.bootstrap;

import com.arun.springsecuritycore.domain.Authority;
import com.arun.springsecuritycore.domain.UserDomain;
import com.arun.springsecuritycore.repository.security.AuthorityRepository;
import com.arun.springsecuritycore.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author arun on 9/12/20
 */

@RequiredArgsConstructor
@Component
@Slf4j
public class UserLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    private Authority ADMIN_ROLE;
    private Authority USER_ROLE;
    private Authority CUSTOMER_ROLE;

    @Override
    public void run(String... args) {
        loadAuthorities();
        loadUsers();
    }

    private void loadUsers() {
        if (userRepository.findByUsername("spring").isEmpty()) {
            Set<Authority> userRoles = new HashSet<>();
            userRoles.add(ADMIN_ROLE);
            UserDomain spring = new UserDomain().setUsername("spring").setPassword(passwordEncoder.encode("guru")).setAuthorities(userRoles);
            userRepository.save(spring);
            log.info("spring user loaded ");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            Set<Authority> userRoles = new HashSet<>();
            userRoles.add(USER_ROLE);
            UserDomain spring = new UserDomain().setUsername("user").setPassword(passwordEncoder.encode("password")).setAuthorities(userRoles);
            userRepository.save(spring);
            log.info("user user loaded ");
        }

        if (userRepository.findByUsername("scott").isEmpty()) {
            Set<Authority> userRoles = new HashSet<>();
            userRoles.add(CUSTOMER_ROLE);
            UserDomain spring = new UserDomain().setUsername("scott").setPassword(passwordEncoder.encode("tiger")).setAuthorities(userRoles);
            userRepository.save(spring);
            log.info("scott user loaded ");
        }
    }

    private void loadAuthorities() {
        ADMIN_ROLE = new Authority().setRoles("ADMIN");
        USER_ROLE = new Authority().setRoles("USER");
        CUSTOMER_ROLE = new Authority().setRoles("CUSTOMER");

        authorityRepository.save(ADMIN_ROLE);
        authorityRepository.save(USER_ROLE);
        authorityRepository.save(CUSTOMER_ROLE);

        log.info("roles loaded ");
    }
}
