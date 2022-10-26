# Description

adding this dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

automatically adds _login_ form when making the requests:

<img src="./images/login.png" width="300">

to quit the session go to `localhost:8080/logout`


## Basic Auth
With this method you need to provide username and password for every request.

<img src="./images/basic-auth.png">

The implementation comes with the new class `ApplicationSecurityConfig` under the `security` package.

```java
@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();    // Basic Authentication
    }
}
```

Which results in the basic login method:

<img src="./images/basic-auth-login.png" width="40%">


