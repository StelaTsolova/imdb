package com.imdb.config.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imdb.util.UtilClass.buildAccessToken;
import static com.imdb.util.UtilClass.buildRefreshToken;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain chain, final Authentication authentication) throws IOException {
        final User user = (User) authentication.getPrincipal();
        final Algorithm algorithm = Algorithm.HMAC256("pass".getBytes());
        final List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        final Date expiresAtAccess = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        final String accessToken = buildAccessToken(user.getUsername(), expiresAtAccess,
                request.getRequestURL().toString(), "roles", roles, algorithm);

        final Date expiresAtRefresh = new Date(System.currentTimeMillis() + 20 * 24 * 60 * 60 * 1000);

        final String refreshToken = buildRefreshToken(user.getUsername(), expiresAtRefresh,
                request.getRequestURL().toString(), algorithm);

        final Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
