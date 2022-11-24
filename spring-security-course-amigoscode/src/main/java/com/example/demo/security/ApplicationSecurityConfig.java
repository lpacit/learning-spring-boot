package com.example.demo.security;

import com.example.demo.auth.ApplicationUserService;
import com.example.demo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.demo.customDSL.MyCustomDslForAuthenticationManager.customDsl;
import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

    private final ApplicationUserService applicationUserService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(ApplicationUserService applicationUserService, PasswordEncoder passwordEncoder) {
        this.applicationUserService = applicationUserService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * If you don't want to use @PreAuthorize annotations, use this code:
     * String managementAntMatcherPath = "/management/api/**";
     *
     * http
     *     .csrf().disable()
     *     .authorizeHttpRequests(authorize -> authorize
     *          .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
     *          .antMatchers("/api/**").hasRole(STUDENT.name())
     *     .antMatchers(HttpMethod.DELETE, managementAntMatcherPath).hasAuthority(STUDENT_WRITE.getPermission())
     *     .antMatchers(HttpMethod.POST, managementAntMatcherPath).hasAuthority(STUDENT_WRITE.getPermission())
     *     .antMatchers(HttpMethod.PUT, managementAntMatcherPath).hasAuthority(STUDENT_WRITE.getPermission())
     *     .antMatchers(HttpMethod.GET, managementAntMatcherPath).hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())
     *     ...)
     *     ...
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager())) NO LONGER SUPPORTED SINCE WebSecurityConfigurerAdapter DEPRECATED
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                        .antMatchers("/api/**").hasRole(STUDENT.name())
                        .anyRequest()
                        .authenticated())
                .httpBasic(Customizer.withDefaults());

        http.apply(customDsl());
        return http.build();
    }

    /**
     * Before implemented like this:
     *     @ Bean
     *     protected InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
     *         UserDetails annaSmithUser = User.builder()
     *                 .username("annasmith")
     *                 .password(passwordEncoder.encode("annasmith"))
     *                 .authorities(STUDENT.getGrantedAuthorities())
     *                 .build();
     *         UserDetails lindaUser = User.builder()
     *                 .username("linda")
     *                 .password(passwordEncoder.encode("linda"))
     *                 .authorities(ADMIN.getGrantedAuthorities())
     *                 .build();
     *         UserDetails tomUser = User.builder()
     *                 .username("tom")
     *                 .password(passwordEncoder.encode("tom"))
     *                 .authorities(ADMINTRAINEE.getGrantedAuthorities())
     *                 .build();
     *         return new InMemoryUserDetailsManager(
     *                 annaSmithUser,
     *                 lindaUser,
     *                 tomUser
     *         );
     *     }
     * */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}
