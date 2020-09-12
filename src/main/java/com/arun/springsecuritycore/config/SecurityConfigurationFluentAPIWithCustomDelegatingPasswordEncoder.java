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
