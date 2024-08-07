package com.sprk.imagegallery.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.sprk.imagegallery.service.UserService;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

        @Bean
        public BCryptPasswordEncoder encoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService) {
                DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
                auth.setUserDetailsService(userService);
                auth.setPasswordEncoder(encoder());

                return auth;
        }

        @Bean
        public OidcUserService getOidcUserService() {
                return new OidcUserService();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler customSuccess)
                        throws Exception {

                http.authorizeHttpRequests(configurer -> configurer
                                .requestMatchers("/", "/{pageNum}").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/imagegallery/user/showLoginForm", "/imagegallery/user/register",
                                                "/imagegallery/user/signup", "/error")
                                .permitAll()
                                .requestMatchers("/imagegallery/user/**").hasRole("USER")
                                .requestMatchers("/imagegallery/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated());
                http.formLogin(form -> form
                                .loginPage("/imagegallery/user/showLoginForm")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .loginProcessingUrl("/authenticateUser")
                                .successHandler(customSuccess).permitAll());

                http.oauth2Login(Customizer.withDefaults());

                http.oauth2Login(oauth -> oauth

                                // .loginPage("/oauth2/authorization/google")
                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                                .oidcUserService(
                                                                getOidcUserService()))

                                .successHandler(customSuccess).permitAll());

                http.logout(logout -> logout.permitAll());
                http.csrf(csrf -> csrf.disable());
                http.exceptionHandling(configurer -> configurer.accessDeniedPage("/imagegallery/accessdenied"));

                return http.build();
        }

}
