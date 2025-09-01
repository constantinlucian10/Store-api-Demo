package com.example.storeapi.Security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

   String path = httpServletRequest.getRequestURI();

    if (path.startsWith("/api/auth") || path.startsWith("/h2-console")) {
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        return;
    }

    String header = httpServletRequest.getHeader("Authorization");
    if(header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
         if(jwtUtil.validate(token)) {
             Claims claims = jwtUtil.parseClaims(token);
             String userName = claims.getSubject();
             String role = claims.get("role", String.class);
             var authentication = new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

             SecurityContextHolder.getContext().setAuthentication(authentication);
         }
    }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
