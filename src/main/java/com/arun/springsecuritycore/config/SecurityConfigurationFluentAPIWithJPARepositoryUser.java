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
                .authorizeRequests(authorize -> {
                    authorize.antMatchers(HttpMethod.GET, "/v2/**").permitAll()
                            .antMatchers("/h2-console/**").permitAll()
                            .antMatchers(HttpMethod.DELETE, "/v1/**").hasRole("ADMIN");
                })
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
