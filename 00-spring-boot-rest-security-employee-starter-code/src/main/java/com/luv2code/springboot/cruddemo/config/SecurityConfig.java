package com.luv2code.springboot.cruddemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    // InMemory Users: means every time the app run, the users are created and stored in memory [RAM] not the DB [desk]
    //Spring will not user the default user/pass or the existed in the app.prop, instead spring will use this InMemory Users
//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//
//
//        UserDetails loz = User.builder()
//                .username("loz")
//                .password("{noop}loz123")
//                .roles("EMPLOYEE")
//                .build();
//
//
//        UserDetails loza = User.builder()
//                .username("loza")
//                .password("{noop}loza123")
//                .roles("EMPLOYEE", "MANAGER")
//                .build();
//
//        UserDetails desha = User.builder()
//                .username("desha")
//                .password("{noop}mudesha") //noop [Encryption Algorithm]=> No Operation {plain text}
//                .roles("EMPLOYEE", "MANAGER", "ADMIN")
//                .build();
//
//
//        return new InMemoryUserDetailsManager(loz, loza, desha);
//    }




    //this will allow spring security to use the security tables(users, authorities) which we previously created in the database to check the authentication
//    @Bean
//    public UserDetailsManager userDetailsManager(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }



    //this will allow spring security to use the custom security tables (user defined not the standard) which we previously created in the database to check the authentication
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager
                .setUsersByUsernameQuery("select user_id, pw, active ve from members where user_id=?");
        userDetailsManager
                .setAuthoritiesByUsernameQuery("select user_id,role from roles where user_id=?");

        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configure -> {

            configure
                    .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
                    .requestMatchers(HttpMethod.GET, "/api/employees/**").hasRole("EMPLOYEE")
                    .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/api/employees").hasRole("MANAGER")
                    .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasRole("MANAGER")
                    .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN");

        });

        //Use HTTP basic authentication
        http.httpBasic(Customizer.withDefaults());

        //CSRF is not required for stateless REST APIs that use POST, PUT, DELETE and PATCH
        http.csrf(csrf -> csrf.disable());

        return http.build();

    }


}
