package com.lib.dto;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationFilter;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.lib.util.JwtUtil;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

   @Autowired
    private JwtUtil jwtUtil;

    @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
           throws ServletException, IOException {
   
       final String authorizationHeader = request.getHeader("Authorization");
   
       String username = null;
       String jwt = null;
   
       if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
           jwt = authorizationHeader.substring(7);
           username = jwtUtil.extractUsername(jwt);
       }
   
       if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
           // You might need to load user details here manually if not using default UserDetailsService
           UsernamePasswordAuthenticationToken authToken =
                   new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
   
           SecurityContextHolder.getContext().setAuthentication(authToken);
       }
   
       chain.doFilter(request, response);
   }

}
