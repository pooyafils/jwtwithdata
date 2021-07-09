package com.example.demosecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

import static com.example.demosecurity.config.ApplicationUserPermission.*;
import static com.example.demosecurity.config.ApplicationUserRole.*;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends
        WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

  /*  @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(securityDataSource);   //other type of the basic auth with db
    }*/

/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER")      //other type of the auth with no db
                .and()
                .withUser("admin").password("{noop}password").roles("ADMIN");

    }*/

/*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }
*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/person/getAll").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.GET,"/person/one/{id}").hasRole(USER.name())
                .antMatchers(HttpMethod.POST, "/person/register").hasRole(USER.name())
                .antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ADMIN.name(),ADMINTRAINER.name())

                .antMatchers("/").permitAll()
                .and().exceptionHandling().accessDeniedPage("/person/access-deniedSS")
                .and()
                .csrf().disable()
                .formLogin().disable()
        ;
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService()  { //basic auth with userdetail memory type\
        UserDetails annaSmithUser=User.builder()
                .username("pass").password(passwordEncoder.encode("pass"))
               // .roles(ADMIN.name())
                .authorities(ADMIN.grantedAuthoritySet())
                .build();
        UserDetails tom=User.builder()
                .username("tom").password(passwordEncoder.encode("pass"))
                //.roles(ADMINTRAINER.name())
                .authorities(ADMINTRAINER.grantedAuthoritySet())
                .build();
        UserDetails user=User.builder()
                .username("user").password(passwordEncoder.encode("pass"))
                //.roles(USER.name())
                .authorities(USER.grantedAuthoritySet())
                .build();
        return new InMemoryUserDetailsManager(annaSmithUser,tom,user);
    }


}