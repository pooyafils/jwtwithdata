# spring security impelemetion type
in this project i implement basic auth ,form base auth,jwt with database or memory saving a password in the way that we explain before is not save and its better to  encrypt use password for this purpose we create the class `PasswordConfig
. each commit on this repository persent a differnt type of the spring security impelemetion. let check it out one by one 
## basic auth
this implemention has a simple logic that you need to :
1. create the class `SecurityConfig` that extend` WebSecurityConfigurerAdapter` abstract class and than override `protected void configure(HttpSecurity http)` method to set up your security policy.
for this implemention we choose database Authentication and Authorization.
2. you need to create the class that manage the database connections and that ovrrider an other method `protected void configure(AuthenticationManagerBuilder auth)` in `SecurityConfig` class
3. whem your are saving a user password in database you must save it in this format `{noop}userpassword` and for user role it must be `ROLE_user role` [related commit](http://handlebarsjs.com/1111).
### im memory db and password encryption
saving a password in the way that we explain before is not save and its better to  encrypt use password for this purpose
1. we create the class `PasswordConfig`
2. now if we want to use in memory  Authentication and Authorization we should create the bean form `UserDetailsService` interface in `SecurityConfig` class.
The standard and most common implementation is the DaoAuthenticationProvider – which retrieves the user details from a simple, read-only user DAO – the UserDetailsService. This User Details Service only has access to the username in order to retrieve the full user entity. This is enough for most scenarios [related commit](http://handlebarsjs.com/2222).
```
@Override
    @Bean
    protected UserDetailsService userDetailsService()  {
           UserDetails myUser= User.builder().username("pass").password("pass").roles("ADMIN").build(); //in memeory
      return new InMemoryUserDetailsManager(myUser);
    }
 ```
 **dont forget to remove `protected void configure(AuthenticationManagerBuilder auth)` from `SecurityConfig` class**<br/>
 if you need to have basic auth security that use  database Authentication and Authorization with password encryption  you need to create you need to comment the `userDetailsService()` method and than 
 [related commit](http://handlebarsjs.com/444444).
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
### how to implement user permissions and roles
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
4. now we must update `SecurityConfig` class which we should first comment the `configAuthentication(AuthenticationManagerBuilder auth)` method that we creatw in the last part and again
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

       
