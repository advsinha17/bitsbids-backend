package com.bitsbids.bitsbids.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
// import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.security.web.DefaultRedirectStrategy;
// import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
// import org.springframework.security.web.savedrequest.SavedRequest;

import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtilityService jwtUtilityService;
    // private static final String SEPARATOR = "|";
    // private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomAuthenticationSuccessHandler(UserService userService, JwtUtilityService jwtUtilityService)

    {
        this.userService = userService;
        this.jwtUtilityService = jwtUtilityService;
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
        user.setPhoneNumber("NOT_SET");
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
        response.addCookie(authCookie);

        // SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request,
        // response);
        // String targetUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() :
        // null;

        // if (targetUrl != null && isUrlSafeAndValid(targetUrl)) {
        // redirectStrategy.sendRedirect(request, response, targetUrl);
        // return;
        // }

        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("message", "Authentication successful");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.getWriter().write(jsonResponse.toString());
    }

    // private boolean isUrlSafeAndValid(String url) {
    // // Implement your URL validation logic here
    // // For example, ensure it's a relative path or a trusted absolute URL
    // return url != null && url.startsWith("/"); // Example validation
    // }

}
