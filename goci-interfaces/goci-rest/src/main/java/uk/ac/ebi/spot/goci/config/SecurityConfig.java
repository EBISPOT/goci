package uk.ac.ebi.spot.goci.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by dwelter on 24/11/16.
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//        auth.inMemoryAuthentication()
////                .withUser("greg").password("turnquist").roles("USER").and()
////                .withUser("ollie").password("gierke").roles("USER", "ADMIN");
//    }

    /**
     * This section defines the security policy for the app.
     * - BASIC authentication is supported (enough for this REST-based demo)
     * - /employees is secured using URL security shown below
     * - CSRF headers are disabled since we are only testing the REST interface,
     *   not a web one.
     *
     * NOTE: GET is not shown which defaults to permitted.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .httpBasic().and()
            .authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/icons/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.POST, "/api").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN").and()
            .csrf().disable();
    }
}