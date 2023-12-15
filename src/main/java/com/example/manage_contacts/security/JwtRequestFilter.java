package com.example.manage_contacts.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) {

        try {
            String jwt = getTokenFromRequest(request);

            if (!StringUtils.hasText(jwt)) {
                filterChain.doFilter(request, response);
                return;


            }
            if (!jwtTokenUtil.validateToken(jwt)) {
                filterChain.doFilter(request, response);
                return;


            }
            Claims claims = jwtTokenUtil.getClaimsFromToken(jwt);
            if (claims == null) {
                filterChain.doFilter(request, response);
                return;


            }
            UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(claims);
            if (authenticationToken == null) {
                filterChain.doFilter(request, response);
                return;


            }
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("failed on set user authentication" + e);
        }

    }

    private UsernamePasswordAuthenticationToken getAuthentication(Claims claims) {
        String username = claims.getSubject();

        if (StringUtils.hasText(username)) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        return null;
    }

    private static String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Bỏ đi phần "Bearer " và trả về token
        }
        return null; // Trả về null nếu không có hoặc định dạng không đúng
    }

}
