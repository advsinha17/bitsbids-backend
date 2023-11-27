package com.bitsbids.bitsbids.Messages;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.bitsbids.bitsbids.Config.JwtUtilityService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtilityService jwtUtilityService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // Extract the JWT token from the cookie
        String jwtToken = extractTokenFromRequest(request);
        if (jwtToken == null || !jwtUtilityService.validateToken(jwtToken)) {
            return false; // Token is invalid, do not establish the WebSocket connection
        }

        // Extract the user's email from the token and store it in the attributes
        String userEmail = jwtUtilityService.getEmailFromToken(jwtToken);
        attributes.put("userEmail", userEmail);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        // Post-handshake logic (usually left empty)
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        if (request.getHeaders().containsKey(HttpHeaders.COOKIE)) {
            String[] cookies = request.getHeaders().getFirst(HttpHeaders.COOKIE).split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("AUTH-TOKEN=")) {
                    return cookie.split("=")[1];
                }
            }
        }
        return null;
    }
}
