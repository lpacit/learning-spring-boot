package com.example.demo.customDSL;

import com.example.demo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class MyCustomDslForAuthenticationManager extends AbstractHttpConfigurer<MyCustomDslForAuthenticationManager, HttpSecurity> {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager));
    }

    public static MyCustomDslForAuthenticationManager customDsl() {
        return new MyCustomDslForAuthenticationManager();
    }
}
