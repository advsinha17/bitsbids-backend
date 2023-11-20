package com.bitsbids.bitsbids.Config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilityService jwtUtilityService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            String jwtToken = extractJwtFromRequest(request);
            if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtUtilityService.validateToken(jwtToken)) {
                    String email = jwtUtilityService.getEmailFromToken(jwtToken);

                    Optional<User> userDetailsOptional = userService.getUserbyEmail(email);

                    if (userDetailsOptional.isPresent()) {
                        User userDetails = userDetailsOptional.get();
                        List<SimpleGrantedAuthority> authorities = Collections
                                .singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AUTH-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
