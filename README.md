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
    
    user name : user 
    password : uuid generated by the server
    

## Basic Authentication where we add a custom user name and password in application.yml

    spring:
      profiles: basic
      security:
        user:
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
    
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username("user")
                    .password("69efb754-2f5f-4990-8772-b3512d7f1922")
                    .roles("USER")
                    .build();
    
            return new InMemoryUserDetailsManager(admin, user);
    
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
