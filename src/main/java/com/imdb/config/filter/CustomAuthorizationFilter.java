package com.imdb.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.imdb.common.utils.jwt.DecodedJWTInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.imdb.common.Constants.PATH_INDEX;
import static com.imdb.common.Constants.PATH_SEARCH;
import static com.imdb.common.utils.TokenUtils.sendErrors;
import static com.imdb.common.utils.jwt.JWTDecoderUtils.decodeJWT;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        if (PATH_SEARCH.equals(request.getServletPath()) || PATH_INDEX.equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
        } else {
            final String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    final DecodedJWTInfo info = decodeJWT(authorizationHeader);
                    final DecodedJWT decodedJWT = info.getDecodedJWT();

                    final String email = decodedJWT.getSubject();
                    final String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                    final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request, response);
                } catch (Exception exception) {
                    sendErrors(response, FORBIDDEN.value(), "error_message",
                            exception.getMessage(), APPLICATION_JSON_VALUE);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}