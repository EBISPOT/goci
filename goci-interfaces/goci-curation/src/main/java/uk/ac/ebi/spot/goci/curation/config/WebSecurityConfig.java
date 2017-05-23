package uk.ac.ebi.spot.goci.curation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;


/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CurrentUserDetailsService currentUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/icons/**").permitAll()
                    .antMatchers("/js/**").permitAll()
                    .antMatchers("/").permitAll()
                        .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/")
                    .failureUrl("/?error")
                    .usernameParameter("useremail")
                    .defaultSuccessUrl("/studies")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll();
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(currentUserDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return currentUserDetailsService;
    }
}
