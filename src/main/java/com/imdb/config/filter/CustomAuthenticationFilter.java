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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.imdb.common.utils.TokenUtils.buildAccessToken;
import static com.imdb.common.utils.TokenUtils.buildRefreshToken;
import static java.time.LocalDate.now;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request,
                                                final HttpServletResponse response) throws AuthenticationException {
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final FilterChain chain,
                                            final Authentication authentication) throws IOException {
        final User user = (User) authentication.getPrincipal();
        final Algorithm algorithm = HMAC256("pass".getBytes());
        final List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        final LocalDate expiresAtAccess = now().plusDays(3);

        final String accessToken = buildAccessToken(user.getUsername(), expiresAtAccess,
                request.getRequestURL().toString(), "roles", roles, algorithm);

        final LocalDate expiresAtRefresh = now().plusMonths(3);

        final String refreshToken = buildRefreshToken(user.getUsername(), expiresAtRefresh,
                request.getRequestURL().toString(), algorithm);

        final Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        tokens.put("email", user.getUsername());

        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), tokens);
    }
}
