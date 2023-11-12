package com.bitsbids.bitsbids.Config;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        String errorMessage = exception.getMessage();
        String encodedErrorMessage = URLEncoder.encode(errorMessage, "UTF-8");
        String redirectUrl = "/home?error=" + encodedErrorMessage;
        response.sendRedirect(redirectUrl);
    }
}
