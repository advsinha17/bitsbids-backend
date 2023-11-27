package com.bitsbids.bitsbids.Config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtilityService jwtUtilityService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String baseUrl;

    public CustomAuthenticationSuccessHandler(UserService userService, JwtUtilityService jwtUtilityService,
            String baseUrl)

    {
        this.userService = userService;
        this.jwtUtilityService = jwtUtilityService;
        this.baseUrl = baseUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("given_name");
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        String username = email.substring(0, 9);

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        userService.addOrUpdateUser(user);

        String jwtToken = jwtUtilityService.generateToken(email);

        Cookie authCookie = new Cookie("AUTH-TOKEN", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(request.isSecure());
        authCookie.setPath("/");
        authCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(authCookie);

        String redirectUrl = baseUrl + "/login/oauth2/code/google";
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}