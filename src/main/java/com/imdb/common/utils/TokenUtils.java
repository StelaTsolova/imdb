package com.imdb.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenUtils {
    public static String buildAccessToken(final String subject,
                                          final LocalDate expiresAt,
                                          final String issuer,
                                          final String claimName,
                                          final List<String> claimValue,
                                          final Algorithm algorithm) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt.atStartOfDay(ZoneId.systemDefault()).toInstant())
                .withIssuer(issuer)
                .withClaim(claimName, claimValue)
                .sign(algorithm);
    }

    public static String buildRefreshToken(final String subject,
                                           final LocalDate expiresAt,
                                           final String issuer,
                                           final Algorithm algorithm) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt.atStartOfDay(ZoneId.systemDefault()).toInstant())
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public static void sendErrors(final HttpServletResponse response,
                                  final int status,
                                  final String errorName,
                                  final String message,
                                  final String contentType) throws IOException {
        response.setStatus(status);
        final Map<String, String> error = new HashMap<>();
        error.put(errorName, message);
        response.setContentType(contentType);

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
