# Spring security impelemetion type
in this project i implement basic auth ,form base auth,jwt with database or memory Authentication and Authorization.security in spring is a long topic but i tried to explain it as much possible in the short readme file
. each commit on this repository persent a differnt type of the spring security impelemetion. let check it out one by one 
## Basic auth
this implemention has a simple logic that you need to :
1. create the class `SecurityConfig` that extend` WebSecurityConfigurerAdapter` abstract class and than override `protected void configure(HttpSecurity http)` method to set up your security policy.
for this implemention we choose database Authentication and Authorization.
2. you need to create the class that manage the database connections `AppConfig` and that ovrrider an other method `protected void configure(AuthenticationManagerBuilder auth)` in `SecurityConfig` class
3. when your are saving a user password in database you must save it in this format `{noop}userpassword` and for user role it must be `ROLE_user role` [related commit](https://github.com/pooyafils/spring-security/tree/1a6179538dbaac9c613e86d6774f6d570b94fd71).
### Im memory db and password encryption
saving a password in the way that we explain before is not safe and its better to  encrypt use password for this purpose
1. we create the class `PasswordConfig`
2. now if we want to use in memory  Authentication and Authorization we should create the bean form `UserDetailsService` interface in `SecurityConfig` class.
The standard and most common implementation is the DaoAuthenticationProvider – which retrieves the user details from a simple, read-only user DAO – the UserDetailsService. This User Details Service only has access to the username in order to retrieve the full user entity. This is enough for most scenarios [related commit](https://github.com/pooyafils/spring-security/tree/8f3012d69e511ba1e01c2bf54398b99adb67eec7).
```
@Override
    @Bean
    protected UserDetailsService userDetailsService()  {
           UserDetails myUser= User.builder().username("pass").password("pass").roles("ADMIN").build(); //in memeory
      return new InMemoryUserDetailsManager(myUser);
    }
 ```
 **dont forget to remove `protected void configure(AuthenticationManagerBuilder auth)` from `SecurityConfig` class**<br/>
 if you need to have basic auth security that use  database Authentication and Authorization with password encryption  you need  to comment the `userDetailsService()` method and than 
 [related commit](https://github.com/pooyafils/spring-security/tree/86c0b0def1a2035d3dd15fa182fd63e731b0d803).
 ```
     @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(securityDataSource)
                .usersByUsernameQuery("select username, password, enabled from users where username=?")
                .authoritiesByUsernameQuery("select username, role from users where username=?")
        ;
    }
```
### How to implement user permissions and roles
role is a high level view of your user.in the any role we have set of the permissions that they specific what user can do in our application.
as you can see we have two  roles here student and admin as you can see each have different permissions.
1. we need create class for permissions
```
public enum ApplicationUserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
```
2. now we need to add dependency
```
<dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>28.1-jre</version>
        </dependency>
 ```
 3. now we need to create roles
 ```
 public enum ApplicationUserRole {
    USER(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(COURSE_READ,COURSE_WRITE,USER_READ,USER_WRITE));
    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }
    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
```
4. now we must update `SecurityConfig` class which we should first comment the `configAuthentication(AuthenticationManagerBuilder auth)` method that we create in the last part and again
create the bean form UserDetailsService interface in `SecurityConfig` class.
```
@Override
    @Bean
    protected UserDetailsService userDetailsService()  {
        UserDetails user=User.builder()
                .username("user").password(passwordEncoder.encode("pass")).roles(USER.name())
                .build();
        return new InMemoryUserDetailsManager(user);
    }
   ```
   now we should update the `antMatcher` in `SecurityConfig` like  ` .antMatchers(HttpMethod.GET, "/person/getAll").hasRole(ADMIN.name())`
### Permissions
so far we have role base Authentication and Authorization in this steps we will add permissions to our system  [related commit](https://github.com/pooyafils/spring-security/tree/54a71f0f49a79b85abe4688145cc593920f72583)
1.  add `getPermissions()` method to  ``ApplicationUserRole`` class
```
  public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
    public Set<SimpleGrantedAuthority> grantedAuthoritySet(){
        Set<SimpleGrantedAuthority>   permissions=     getPermissions().stream().map(permission->new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return permissions;

    }
```
2. Now there is two part of the code that need to updated first we need to call `authorities(USER.grantedAuthoritySet())` method  in `userDetailsService()` to get the user permissions and role. second we need to 
```
//to check if user has a permission to access this url
.antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
```
## Form base Authentication and Authorization
as you see in basic auth we need to send user name and passowrd everytime we want o access the url. in form base  Authentication and Authorization  you only send a username and password once and than a session will be created for you so later on when you want to access that url spring security will check if your session is valid so you can aceess the url otherwise you need to send username and password again
1. you only need to change `protected void configure(HttpSecurity http)` and crate the controller for login and Access Denied Page
 [related commit](https://github.com/pooyafils/spring-security/tree/78d71a3b1bef8beb0cab52222701d0dfc0909920).
```
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() 
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll() // urls that must not be under spring security
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/courses",true)
                .and().rememberMe().tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21)) //how long session must be valid
                .key("something").and()
                .logout().logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))
                .clearAuthentication(true)
                .invalidateHttpSession(true).deleteCookies("JSESSIONID","remember-me")
                .logoutSuccessUrl("/login"); //custom login page
    }
 ```
## JWT
jwt means  json web token. after user complete Authentication and Authorization spring security will generate the token and later on when user want to access url,  user only send token to the application and than spring security will check out if user can access to the url or not
1. add maven dependency  [related commit](https://github.com/pooyafils/spring-security/tree/c141e4b371cb93cafa0e7c5deee88205dd44aa29).
```
<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.10.7</version>
		</dependency>

		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.10.7</version>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.10.7</version>
			<scope>runtime</scope>
		</dependency>
  ```
 2. we need to develop `JwtUsernamePasswordAuthenticationFilter` class to do authentication and generate token and send it back to the client. we need also the create the class `UsernameAndPasswordAuthenticationRequest` to take the username and password
 ```
 public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            Authentication authentications = authenticationManager.authenticate(authentication);
            return authentications;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
      String token=  Jwts.builder()
                .setSubject(authResult.getName()).claim("authorities",authResult.getAuthorities())
                .setIssuedAt(new Date()).setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(2)))
                .signWith(Keys.hmacShaKeyFor("securesecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecure".getBytes()))
                .compact();
      response.addHeader("Authorization","Bearer "+token);
    }
}
```
3. verify the token--> now we need to verify the token that we generate for client.we need to create and develop Jwtconfig , JwtSecretKey ,JwtTokenVerifier and add some external properties to the application.properties.we need to do some changes in `JwtUsernamePasswordAuthenticationFilter` class
```
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,JwtConfig jwtConfig,SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig=jwtConfig;
        this.secretKey=secretKey;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            Authentication authentications = authenticationManager.authenticate(authentication);
            return authentications;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
      //  String key="securesecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecure";
      String token=  Jwts.builder()
                .setSubject(authResult.getName()).claim("authorities",authResult.getAuthorities())
                .setIssuedAt(new Date()).setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
               // .signWith(Keys.hmacShaKeyFor(key.getBytes()))
              .signWith(secretKey)
                .compact();
     // response.addHeader("Authorization","Bearer "+token);
        response.addHeader(jwtConfig.getAuthorizationHeader(),jwtConfig.getTokenPrefix()+token);

    }
}
```
4. in this implementation we are having in memory  but we set up the basement for you in order to have db in future. so first we need to create `ApplicationUserService` class that implement `UserDetailsService` interface we need to create the other class in order to load users data and send it to  `ApplicationUserService` class . for this reason we should create `FakeApplicationUserDaoService` class and inject it to the `ApplicationUserService` class
5. in the last step we need to enject the SecretKey,JwtConfig,ApplicationUserService,PasswordEncoder to the ` SecurityConfig` class and set the new filter for jwt in `configure(HttpSecurity http) ` method
```
public class SecurityConfig extends
        WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    public SecurityConfig(PasswordEncoder passwordEncoder,ApplicationUserService applicationUserService
  , SecretKey secretKey, JwtConfig jwtConfig) {
        this.applicationUserService=applicationUserService;
        this.passwordEncoder = passwordEncoder;
        this.secretKey=secretKey;
        this.jwtConfig=jwtConfig;
    }
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager(),jwtConfig,secretKey))
                .addFilterAfter(new JwtTokenVerifier(secretKey,jwtConfig),JwtUsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers(HttpMethod.GET, "/person/getAll").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.GET,"/person/one/{id}").hasRole(USER.name())
                .anyRequest()
                .authenticated();

    }}
   ```

