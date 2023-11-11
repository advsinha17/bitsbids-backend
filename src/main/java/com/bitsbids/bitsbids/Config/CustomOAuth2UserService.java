package com.bitsbids.bitsbids.Config;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // First, load the user details using the default implementation
        OAuth2User user = super.loadUser(userRequest);
        logger.debug("User attributes received from OAuth2 provider: {}", user.getAttributes());

        // Then, implement your custom logic
        // For example, check if the user's email matches a certain pattern
        String email = user.getAttribute("email");
        logger.debug("Email extracted from user attributes: {}", email);

        // Check if the email matches your specific pattern
        if (email != null && !email.matches("h20[0-9]{8}@hyderabad.bits-pilani.ac.in")) {
            logger.warn("Access denied for user with email: {}", email);
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_login"),
                    "ERROR: Only accessible by FD BITS Hyderabad students.");
        }

        // Return the OAuth2User object if the email matches the pattern
        return user;
    }
}
