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

**_NOTE_**: In order to have [this login page](./images/login.png) displayed, just add `.formLogin()`. 

## Whitelist URLs with Ant Matchers
If you want to add an `index.html` file as the landing page for `localhost:8080`, every time you try to want to access this page you need to provide authentication.
Instead, you want to have clear access to this page, and provide credentials when making API calls.

There are a few options available:
+ Method suggested by Spring docs. Inside of `ApplicationSecurityConfig` class:
```java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().antMatchers("/", "index", "/css/*", "/js/*");
}
```
+ Other method figured out by myself. Inside of `filterChain` method:
```java
http
        .authorizeHttpRequests()
        .antMatchers("/", "index.html", "/css/*", "/js/")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic(withDefaults()).formLogin();
```

## Application Users

+ This is for adding users in an **_in-memory_** datastore:
```java
@Bean
public InMemoryUserDetailsManager userDetailsService() {
    UserDetails annaSmithUser = User.withDefaultPasswordEncoder()
        .username("annasmith")
        .password("annasmith")
        .roles("STUDENT")   // ROLE_STUDENT
        .build();
    return new InMemoryUserDetailsManager(annaSmithUser);
}
```

### Adding custom password encoding

+ Created `PasswordConfig` class:

```java
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
```

+ Inside of `ApplicationSecurityConfig` class:

```java
@Bean
public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails annaSmithUser = User.builder()
        .username("annasmith")
        .password(passwordEncoder.encode("annasmith"))
        .roles("STUDENT")   // ROLE_STUDENT
        .build();
    return new InMemoryUserDetailsManager(annaSmithUser);
}
```

## Roles and Permissions
To manage users roles and permissions, you can add enums like theese:
+ First, create a enum for the roles: `ApplicationUserRole`

```java
public enum ApplicationUserRole {
    STUDENT(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(
            STUDENT_WRITE,
            STUDENT_READ,
            COURSE_WRITE,
            COURSE_READ
    ));

    private final Set<ApplicationUserPermission> permissions;


    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
```

+ Second, create an enum like this:
```java
public enum ApplicationUserPermission {
    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    COURSE_READ("courses:read"),
    COURSE_WRITE("courses:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
```

Now you can add permissions to your user like this:

```java
...
.password(passwordEncoder.encode("linda"))
.roles(ADMIN.name())
...
```

**_NOTE_**: a dependency has been added to the pom previosly:
```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```
