# Description

adding this dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

automatically adds _login_ form when making the requests:

<img src="./images/login.png" width="40%">

to quit the session go to `localhost:8080/logout`


## Basic Auth
With this username and password are provided for every request. Hence, there is no way to _logout_.

<img src="./images/basic-auth.png" width="50%">

The implementation comes with the new class `ApplicationSecurityConfig` under the `security` package.

```java
@Configuration
public class ApplicationSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> 
                        authz.anyRequest().authenticated()
                ).httpBasic(withDefaults());
        return http.build();
    }
}
```

Which results in the basic login method:

<img src="./images/basic-auth-login.png" width="40%">

## Whitelist URLs with Ant Matchers
If you want to add an `index.html` file as the landing page for `localhost:8080`, every time you try to want to access this page you need to provide authentication.
Instead, you want to have clear access to this page, and provide credentials when making API calls.

