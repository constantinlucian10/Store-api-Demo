package com.example.storeapi.Controller;

import com.example.storeapi.Security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthenticationController(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/api/auth/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String userName = request.get("username");
        String password = request.get("password");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");

        String token = jwtUtil.generateToken(userName, role);

        return Map.of("token", token);
    }

    @GetMapping("/api/auth/user")
    public Map<String, String> currentUser(Authentication authentication) {
        String roles = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return Map.of(
                "username", authentication.getName(),
                "roles", roles
        );
    }
}
