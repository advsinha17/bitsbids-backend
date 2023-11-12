package com.bitsbids.bitsbids.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import com.bitsbids.bitsbids.Users.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> {
            auth.anyRequest().authenticated();
        })
                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(this.oauth2UserService()))
                        .successHandler(new CustomAuthenticationSuccessHandler(userService))
                        .failureHandler(new CustomAuthenticationFailureHandler()));

        return http.build();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return new OAuth2UserService<OAuth2UserRequest, OAuth2User>() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User oAuth2User = delegate.loadUser(userRequest);
                String email = oAuth2User.getAttribute("email");
                if (email != null && !email.matches("f20[0-9]{6}@hyderabad.bits-pilani.ac.in")) {
                    throw new OAuth2AuthenticationException(new OAuth2Error("invalid_login"),
                            "ERROR: Only accessible by FD BITS Hyderabad students.");
                }
                return oAuth2User;
            }
        };

    }
}
