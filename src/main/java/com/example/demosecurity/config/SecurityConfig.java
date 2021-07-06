package com.example.demosecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends
        WebSecurityConfigurerAdapter {
    //private final PasswordEncoder passwordEncoder;

    // public SecurityConfig(PasswordEncoder passwordEncoder) {
    //    this.passwordEncoder = passwordEncoder;
    //  }
    @Autowired
    private DataSource securityDataSource;
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(securityDataSource)
                .usersByUsernameQuery("select username, password, enabled from users where username=?")
                .authoritiesByUsernameQuery("select username, role from users where username=?")
        ;
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
                .antMatchers(HttpMethod.GET, "/person/getAll").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/person/register").hasRole("USER")
                .antMatchers("/").permitAll()
                .and().exceptionHandling().accessDeniedPage("/person/access-deniedSS")
                .and()
                .csrf().disable()
                .formLogin().disable();
    }

/*    @Override
   @Bean
    protected UserDetailsService userDetailsService()  { //basic auth with userdetail memory type
        return new JdbcUserDetailsManager(securityDataSource);
      UserDetails myUser= User.builder().username("pass").password(passwordEncoder.encode("pass")).roles("ADMIN").build(); //in memeory
    return new InMemoryUserDetailsManager(myUser);
}*/


}

