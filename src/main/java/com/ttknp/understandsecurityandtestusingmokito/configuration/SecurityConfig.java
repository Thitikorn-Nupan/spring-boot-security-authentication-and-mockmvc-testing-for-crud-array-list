package com.ttknp.understandsecurityandtestusingmokito.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public static PasswordEncoder usePasswordEncoder() { // have to config it to be once of beans
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService loadUserDetailsService() {
        // Way to define username / password in memory for authenticate
        User.UserBuilder userBuilder = User.builder(); // withDefaultPasswordEncoder() Deprecated. use instead builder() it's worked
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                userBuilder
                .username("user")
                .password(usePasswordEncoder().encode("12345"))
                // **** Should use once!
                // **** Notice in apiFilterChain(...) config
                // .roles("NORMAL") // you have to map with prefix as ROLE_NORMAL
                .authorities("read") // or use authorities no need prefix
                // ** read more at => www.geeksforgeeks.org/difference-between-hasrole-and-hasauthority-in-spring-security
                .build()
        );
        manager.createUser(
                userBuilder
                .username("admin")
                .password(usePasswordEncoder().encode("12345"))
                .authorities("read","write")
                .build()
        );
        manager.createUser(
                userBuilder
                .username("robot")
                .password(usePasswordEncoder().encode("12345"))
                 .authorities("none")
                .build()
        );
        return manager;
    }

    /**
        The configuration creates a Servlet Filter known as the springSecurityFilterChain
        which is responsible for all the security (protecting the application URLs,
        validating submitted username and passwords, redirecting to the log in form, and so on)
        ## Basic Authentication DO NOT use cookies
    */
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity httpSecurity)  throws Exception {
        log.info("users authenticate with HTTP Basic authentication");

        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                                // ** Note you have to should once! For authentication , this case i use hasAuthority instead hasRole
                                .requestMatchers(HttpMethod.GET,"/api/","/api").hasAuthority("read") // ** Who has role as read , Can access
                                .requestMatchers(HttpMethod.GET,"/api/dogs").hasAuthority("write")
                                .requestMatchers(HttpMethod.GET,"/api/dogs/by").hasAuthority("write")
                                .requestMatchers(HttpMethod.GET,"/api/dogs/**").hasAuthority("write")
                                .requestMatchers(HttpMethod.POST,"/api/dogs").hasAuthority("write")
                                .requestMatchers(HttpMethod.PUT,"/api/dogs/**").hasAuthority("write")
                                .requestMatchers(HttpMethod.DELETE,"/api/dogs/**").hasAuthority("write")
                                .anyRequest().authenticated()
                );

        httpSecurity
                .httpBasic()
                // this config below works for logout after close browser (Work only private browser)
                .and()
                .logout()
                .clearAuthentication(true)
                // ** If, you only call as get method works
                // the reason for the forbidden response that I was getting for the POST APIs was that Spring security was waiting for csrf token for these POST requests
                // because CSRF protection is enabled by default in spring security.
                // you can disable csrf here, you should enable it before using in production
                .and()
                .csrf()
                .disable();

        return httpSecurity
                .build();
    }
}
