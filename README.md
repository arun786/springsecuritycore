# Step by Step Spring Security

## Basic Configuration for the rest API

### Step 1 :

   is to add a dependency in build.gradle

    implementation 'org.springframework.boot:spring-boot-starter-security'
 

### Step 2 :

Add few records in the h2 database

    package com.arun.springsecuritycore.bootstrap;
    
    import com.arun.springsecuritycore.domain.Student;
    import com.arun.springsecuritycore.model.StudentDomain;
    import com.arun.springsecuritycore.repository.StudentRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.stereotype.Repository;
    
    /**
     * @author arun on 8/29/20
     */
    
    @Repository
    public class StudentLoader implements CommandLineRunner {
    
        private final StudentRepository studentRepository;
    
        @Autowired
        public StudentLoader(StudentRepository studentRepository) {
            this.studentRepository = studentRepository;
        }
    
        @Override
        public void run(String... args) {
            for (int i = 0; i < 10; i++) {
                StudentDomain studentDomain = new StudentDomain()
                        .setAge(10)
                        .setName("Arun" + (i + 1))
                        .setStandard("1");
                studentRepository.save(studentDomain);
            }
        }
    }


### Step 3:

In application.yml file

    server:
      port: 12000
    
    
    spring:
      h2:
        console:
          enabled: true
      datasource:
        url: jdbc:h2:mem:testdb
        driver-class-name: org.h2.Driver
        username: sa
        password: password
      jpa:
        database-platform: org.hibernate.dialect.H2Dialect
        
    

## Basic Authentication where we add a dependency in build.gradle file

    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    This generates a uuid when the server starts.
    
    userDomain name : userDomain 
    password : uuid generated by the server
    

## Basic Authentication where we add a custom userDomain name and password in application.yml

    spring:
      profiles: basic
      security:
        userDomain:
          name: student
          password: dXNlcjo2N2VlM2IzZC1iODRmLTQxYzgtOWUxMS01ZTg3ZjAwNTE3MWY=
          
          

## Spring boot application, where the secured api is called by another rest api

[Client call](https://github.com/arun786/springsecuritycoreclient/blob/master/README.md) 
    

## Filter out urls to surpass the security

### Note : if you have multiple security configurer adaptor we annotate it with @Order(100), so that executes first 

Here I want to have no security for url starting with /v2

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    
    /**
     * @author arun on 9/3/20
     */
    
    @EnableWebSecurity
    @Configuration
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
        /**
         * To configure security such that the url starting with /v2 does not require authorization
         *
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers("/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    }

## In Memory Authentication (use of User Details)

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.core.userdetails.User;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.provisioning.InMemoryUserDetailsManager;
    
    /**
     * @author arun on 9/3/20
     */
    
    @EnableWebSecurity
    @Configuration
    @Profile("basic_user")
    @Order(102)
    public class SecurityConfigCustomized extends WebSecurityConfigurerAdapter {
    
        /**
         * To configure security such that the url starting with /v2 does not require authentication
         *
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        @Override
        @Bean
        protected UserDetailsService userDetailsService() {
    
            UserDetails admin = User.withDefaultPasswordEncoder()
                    .username("admin")
                    .password("3cab46bd-d565-4e53-b276-9a0ce5e13dd7")
                    .roles("ADMIN")
                    .build();
    
            UserDetails userDomain = User.withDefaultPasswordEncoder()
                    .username("userDomain")
                    .password("69efb754-2f5f-4990-8772-b3512d7f1922")
                    .roles("USER")
                    .build();
    
            return new InMemoryUserDetailsManager(admin, userDomain);
    
        }
    }


## In Memory Authentication (fluent api)

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    
    /**
     * @author arun on 9/4/20
     */
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user")
    public class SecurityConfigurationFluentAPI extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("{noop}student")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("{noop}admin")
                    .roles("ADMIN");
        }
    }

    
##  In Memory Authentication (fluent api) with password Encoder


    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.password.NoOpPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
    /**
     * @author arun on 9/4/20
     */
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_password_encoder")
    public class SecurityConfigurationFluentAPIWithPasswordEncoder extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("student")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("admin")
                    .roles("ADMIN");
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }


##  In Memory Authentication (fluent api) with ldap password Encoder

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_ldap_password_encoder")
    public class SecurityConfigurationFluentAPIWithLdapEncoder extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("{SSHA}v5nAsGzRuA+PWyveT2jH7TqU8eNMNBZNYv7PFA==")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("{SSHA}81/VhsnqiDlulryB5ag3K6vvEZZeT2iAxPG5Pg==")
                    .roles("ADMIN");
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new LdapShaPasswordEncoder();
        }
    }



##  In Memory Authentication (fluent api) with sha256 password Encoder

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.crypto.password.StandardPasswordEncoder;
    
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_sha256_password_encoder")
    public class SecurityConfigurationFluentAPIWithSha256Encoder extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("67df0c8329a0bfc0650395cadfbd88437a98272e9d5fcdb6da4f37ad6ddbd2d21d1864bacea8f45e")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("e956ed1382e539dbf4e6a5c0309eb8fc4bb1dcaa71c819af19e8bdae87b1d77af141a0538dd09881")
                    .roles("ADMIN");
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new StandardPasswordEncoder();
        }
    }


##  In Memory Authentication (fluent api) with bcrypt password Encoder

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_bencrypt_password_encoder")
    public class SecurityConfigurationFluentAPIWithBCryptEncoder extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("$2a$10$DAZWSXKXiWJcAibCS8.CguwekNABqhTwT8exLy8Z//MZZAJVSakuW")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("$2a$10$iofIqijAEgQcFpwjgvGdgO1iRgjvV6gTXHWqyWGz.UtFzwoYTNPj.")
                    .roles("ADMIN");
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }


##  In Memory Authentication (fluent api) with delegating password Encoder

    package com.arun.springsecuritycore.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.factory.PasswordEncoderFactories;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_delegating_password_encoder")
    public class SecurityConfigurationFluentAPIWithDelegatingPasswordEncoder extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        /**
         * student will use bcrypt
         * and admin will use sha256
         *
         * @param auth
         * @throws Exception
         */
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("{bcrypt}$2a$10$DAZWSXKXiWJcAibCS8.CguwekNABqhTwT8exLy8Z//MZZAJVSakuW")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("{sha256}e956ed1382e539dbf4e6a5c0309eb8fc4bb1dcaa71c819af19e8bdae87b1d77af141a0538dd09881")
                    .roles("ADMIN");
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }
    }


# Custom Password Encoder

    package com.arun.springsecuritycore.security;
    
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
    import java.util.HashMap;
    import java.util.Map;
    
    /**
     * @author arun on 9/7/20
     */
    public class StudentPasswordEncoderFactory {
    
        public static PasswordEncoder createDelegatingPasswordEncoder() {
            String encodingId = "bcrypt";
            Map<String, PasswordEncoder> encoders = new HashMap<>();
            encoders.put(encodingId, new BCryptPasswordEncoder());
            encoders.put("bcrypt15", new BCryptPasswordEncoder(15));
            encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
            encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
            encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
    
            return new DelegatingPasswordEncoder(encodingId, encoders);
        }
    }
    
Add the securityAdaptor

    package com.arun.springsecuritycore.config;
    
    import com.arun.springsecuritycore.security.StudentPasswordEncoderFactory;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
  
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_custom_delegating_password_encoder")
    public class SecurityConfigurationFluentAPIWithCustomDelegatingPasswordEncoder extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
        }
    
        /**
         * student will use bcrypt
         * and admin will use sha256
         *
         * @param auth
         * @throws Exception
         */
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("student")
                    .password("{bcrypt15}$2a$15$sNBm/n5HS/VAMciuivoGJuScgpkPeqUw5I7af0p.2MFASDqxtG5mG")
                    .roles("USER")
                    .and()
                    .withUser("admin")
                    .password("{sha256}e956ed1382e539dbf4e6a5c0309eb8fc4bb1dcaa71c819af19e8bdae87b1d77af141a0538dd09881")
                    .roles("ADMIN");
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return StudentPasswordEncoderFactory.createDelegatingPasswordEncoder();
        }
    }


# use database to store the credentials and the roles

Create a userDomain and authority table with many to many relation

User table

    package com.arun.springsecuritycore.domain;
    
    import lombok.Getter;
    import lombok.Setter;
    
    import javax.persistence.*;
    import java.util.Set;
    
    /**
     * @author arun on 9/11/20
     */
    
    @Getter
    @Setter
    @Entity
    public class User {
    
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;
        private String username;
        private String password;
    
        @ManyToMany(cascade = CascadeType.MERGE)
        @JoinTable(name = "user_authority",
                joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
                inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
        private Set<Authority> authorities;
    
        private Boolean accountNotExpired = true;
        private Boolean accountNotLocked = true;
        private Boolean credentialsNotExpired = true;
        private Boolean enabled = true;
    }


Authority table

    package com.arun.springsecuritycore.domain;
    
    import lombok.Getter;
    import lombok.Setter;
    
    import javax.persistence.*;
    import java.util.Set;
    
    /**
     * @author arun on 9/11/20
     */
    
    @Getter
    @Setter
    @Entity
    public class Authority {
    
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;
        private String roles;
    
        @ManyToMany(mappedBy = "authorities")
        private Set<User> userDomain;
    }


Add the repository class 

UserRepository

    package com.arun.springsecuritycore.repository.security;
    
    import com.arun.springsecuritycore.domain.UserDomain;
    import org.springframework.data.jpa.repository.JpaRepository;
    
    import java.util.Optional;
    
    /**
     * @author arun on 9/11/20
     */
    public interface UserRepository extends JpaRepository<User, Integer> {
    
        Optional<User> findByUsername(String userName);
    }

AuthorityRepository

    package com.arun.springsecuritycore.repository.security;
    
    import com.arun.springsecuritycore.domain.Authority;
    import org.springframework.data.jpa.repository.JpaRepository;
    
    /**
     * @author arun on 9/11/20
     */
    public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    }


# Load the userDomain on start of the server

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
                User spring = new User().setUsername("spring").setPassword(passwordEncoder.encode("guru")).setAuthorities(userRoles);
                userRepository.save(spring);
                log.info("spring userDomain loaded ");
            }
    
            if (userRepository.findByUsername("userDomain").isEmpty()) {
                Set<Authority> userRoles = new HashSet<>();
                userRoles.add(USER_ROLE);
                User spring = new User().setUsername("userDomain").setPassword(passwordEncoder.encode("password")).setAuthorities(userRoles);
                userRepository.save(spring);
                log.info("userDomain userDomain loaded ");
            }
    
            if (userRepository.findByUsername("scott").isEmpty()) {
                Set<Authority> userRoles = new HashSet<>();
                userRoles.add(CUSTOMER_ROLE);
                User spring = new User().setUsername("scott").setPassword(passwordEncoder.encode("tiger")).setAuthorities(userRoles);
                userRepository.save(spring);
                log.info("scott userDomain loaded ");
            }
        }
    
        private void loadAuthorities() {
            ADMIN_ROLE = new Authority().setRoles("ADMIN");
            USER_ROLE = new Authority().setRoles("USER");
            CUSTOMER_ROLE = new Authority().setRoles("customer");
    
            authorityRepository.save(ADMIN_ROLE);
            authorityRepository.save(USER_ROLE);
            authorityRepository.save(CUSTOMER_ROLE);
    
            log.info("roles loaded ");
        }
    }

# configure the UserDetailsService

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
    
 # websecurityadapter doesnot require config method overloaded
 
    package com.arun.springsecuritycore.config;
    
    import com.arun.springsecuritycore.security.StudentPasswordEncoderFactory;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.password.PasswordEncoder;
    
    
    @EnableWebSecurity
    @Configuration
    @Order(100)
    @Profile("fluent_api_user_jpa_repository")
    public class SecurityConfigurationFluentAPIWithJPARepositoryUser extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(auth -> auth.antMatchers("/h2-console/**").permitAll())
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll())
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic();
    
            http.csrf().disable();
            http.headers().frameOptions().sameOrigin();
        }
    
        @Bean
        public PasswordEncoder passwordEncoder() {
            return StudentPasswordEncoderFactory.createDelegatingPasswordEncoder();
        }
    
        //configure method is not required
    }

