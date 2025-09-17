package com.soa.trello.tasks.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.nio.charset.StandardCharsets;

public class JwtAuthFilter extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc){
        try {
            String a=req.getHeader("Authorization");
            if (a!=null && a.startsWith("Bearer ")){
                var c= Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(a.substring(7)).getBody();
                var auth=new UsernamePasswordAuthenticationToken(c.get("userId"), null, java.util.List.of(() -> "ROLE_"+c.get("role")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {}
        try{ fc.doFilter(req,res);}catch(Exception e){ throw new RuntimeException(e);}
    }
}
