package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.demo.security.ApplicationUserPermission.COURSE_WRITE;
import static com.example.demo.security.ApplicationUserRole.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ApplicationSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String managementAntMatcherPath = "/management/api/**";
        http
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                        .antMatchers("/api/**").hasRole(STUDENT.name())

                        // Here we are basically saying that DELETE, POST and PUT can be done by someone with COURSE_WRITE permission
                        .antMatchers(HttpMethod.DELETE, managementAntMatcherPath).hasAuthority(COURSE_WRITE.getPermission())
                        .antMatchers(HttpMethod.POST, managementAntMatcherPath).hasAuthority(COURSE_WRITE.getPermission())
                        .antMatchers(HttpMethod.PUT, managementAntMatcherPath).hasAuthority(COURSE_WRITE.getPermission())

                        // Here we are saying that this path is restricted to ADMIN and ADMINTRAINEE
                        .antMatchers(HttpMethod.GET, managementAntMatcherPath).hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(withDefaults()).formLogin();

        return http.build();
    }

    @Bean
    protected InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails annaSmithUser = User.builder()
                .username("annasmith")
                .password(passwordEncoder.encode("annasmith"))
//                .roles(STUDENT.name())   // ROLE_STUDENT
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        UserDetails lindaUser = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("linda"))
//                .roles(ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        UserDetails tomUser = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("tom"))
//                .roles(ADMINTRAINEE.name())
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
                annaSmithUser,
                lindaUser,
                tomUser
        );
    }
}
