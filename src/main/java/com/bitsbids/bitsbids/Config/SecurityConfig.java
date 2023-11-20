package com.bitsbids.bitsbids.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bitsbids.bitsbids.Users.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtilityService jwtUtilityService;

    @Autowired
    InMemoryClientRegistrationRepository clientRegistrationRepository;

    @Value("${baseUrl}")
    private String baseUrl;

    @Bean
    JwtAuthenticationTokenFilter authenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/products").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/products").authenticated();
                    auth.requestMatchers("/api/auth/check").authenticated();
                    auth.anyRequest().permitAll();
                })
                .oauth2Login(login -> login
                        // .authorizationEndpoint(authorizationEndpointConfig ->
                        // authorizationEndpointConfig
                        // .authorizationRequestResolver(oAuth2AuthorizationRequestResolver()))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(this.oauth2UserService()))
                        .successHandler(new CustomAuthenticationSuccessHandler(userService, jwtUtilityService))
                        .failureHandler(new CustomAuthenticationFailureHandler())
                        .defaultSuccessUrl((baseUrl + "/login/oauth2/code/google")))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/google")));

        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/google")));

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

    // @Bean
    // OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver() {
    // DefaultOAuth2AuthorizationRequestResolver
    // defaultOAuth2AuthorizationRequestResolver = new
    // DefaultOAuth2AuthorizationRequestResolver(
    // clientRegistrationRepository, "/oauth2/authoriation/google");
    // return new
    // WithRefererOAuth2AuthorizationRequestResolver(defaultOAuth2AuthorizationRequestResolver);
    // }

    // static class WithRefererOAuth2AuthorizationRequestResolver implements
    // OAuth2AuthorizationRequestResolver {

    // private final DefaultOAuth2AuthorizationRequestResolver delegate;
    // private static final String SEPARATOR = "|";

    // public
    // WithRefererOAuth2AuthorizationRequestResolver(DefaultOAuth2AuthorizationRequestResolver
    // delegate) {
    // this.delegate = delegate;
    // }

    // @Override
    // public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    // OAuth2AuthorizationRequest oAuth2AuthorizationRequest =
    // delegate.resolve(request);
    // return patchState(oAuth2AuthorizationRequest);
    // }

    // @Override
    // public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String
    // clientRegistrationId) {
    // OAuth2AuthorizationRequest oAuth2AuthorizationRequest =
    // delegate.resolve(request, clientRegistrationId);
    // return patchState(oAuth2AuthorizationRequest);
    // }

    // private OAuth2AuthorizationRequest patchState(OAuth2AuthorizationRequest
    // auth2AuthorizationRequest) {
    // if (auth2AuthorizationRequest == null) {
    // return null;
    // }
    // return OAuth2AuthorizationRequest.from(auth2AuthorizationRequest)
    // .state(auth2AuthorizationRequest.getState() +
    // getSeparatorRefererOrEmpty()).build();
    // }

    // private String getSeparatorRefererOrEmpty() {
    // HttpServletRequest currentHttpRequest = getCurrentHttpRequest();
    // if (currentHttpRequest != null) {
    // // Extract the 'redirect' query parameter instead of the 'Referer' header
    // String redirectUrl = currentHttpRequest.getParameter("redirect");
    // if (StringUtils.hasLength(redirectUrl)) {
    // return SEPARATOR + redirectUrl;
    // }
    // }
    // return "";
    // }

    // private HttpServletRequest getCurrentHttpRequest() {
    // ServletRequestAttributes attrs = (ServletRequestAttributes)
    // RequestContextHolder.getRequestAttributes();
    // return attrs != null ? attrs.getRequest() : null;
    // }

    // }
}
