package com.finance.financeProject.JwtAccessToken;


import com.finance.financeProject.serviceImpl.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX="Bearer ";
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header==null
                || !header.startsWith(BEARER_PREFIX)
                || SecurityContextHolder.getContext().getAuthentication() !=null){
            filterChain.doFilter(request,response);
            return;
        }
        try {
            String  token= header.substring(BEARER_PREFIX.length());
            String  email = jwtService.extraSubject(token);

            UserDetails userDetails= customUserService.loadUserByUsername(email) ;

            UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            // to set below details into security context holder
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        }catch (JwtException
              | IllegalArgumentException
                | UsernameNotFoundException e){
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request,response);
    }
}
