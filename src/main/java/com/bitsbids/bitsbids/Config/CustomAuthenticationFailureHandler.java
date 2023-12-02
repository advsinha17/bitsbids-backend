package com.bitsbids.bitsbids.Config;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String baseUrl;

    public CustomAuthenticationFailureHandler(String baseUrl)

    {
        this.baseUrl = baseUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
        String errorMessage = "Login failed! Only accessible by FD BITS Hyderabad students.";
        String encodedErrorMessage = URLEncoder.encode(errorMessage, "UTF-8");
        String redirectUrl = baseUrl + "/login/oauth2/code/google?error=" + encodedErrorMessage;
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
