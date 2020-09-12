package com.arun.springsecuritycore.security;

import com.arun.springsecuritycore.domain.Authority;
import com.arun.springsecuritycore.domain.UserDomain;
import com.arun.springsecuritycore.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author arun on 9/12/20
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loaded user " + username);
        UserDomain userDomain = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user name : " + username + " not found"));

        return new User(userDomain.getUsername(), userDomain.getPassword(), userDomain.getEnabled(),
                userDomain.getAccountNotExpired(), userDomain.getCredentialsNotExpired(), userDomain.getAccountNotLocked(),
                convertToSpringAuthorities(userDomain.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        if (authorities != null && authorities.size() > 0) {
            return authorities.stream()
                    .map(Authority::getRoles)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        return new HashSet<>();
    }
}
