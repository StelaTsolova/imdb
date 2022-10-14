package com.imdb.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilClass {
    public static final String PATH_INDEX = "/";
    public static final String PATH_LOGIN = "/login";
    public static final String PATH_REGISTER = "/register";
    public static final String PATH_TOKEN_REFRESH = "/token/refresh";
    public static final String PATH_MOVIES = "/movies";
    public static final String PATH_ID = "/{id}";
    public static final String PATH_PICTURE = "/picture";
    public static final String PATH_SEARCH = "/search";
    public static final String PATH_CONTROL = "/control";
    public static final String PATH_MY = "/my";

    public static Map<String, String> getErrorMessages(final List<ObjectError> allErrors) {
        final Map<String, String> errorMessages = new HashMap<>();

        for (Object object : allErrors) {
            if (object instanceof FieldError fieldError) {
                errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return errorMessages;
    }

    public static String getErrorMessagesToString(final Map<String, String> errorMessages) {
        StringBuilder stringBuilder = new StringBuilder().append("[");

        String result = errorMessages.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));

        stringBuilder.append(result);
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public static String buildAccessToken(final String subject, final Date expiresAt, final String issuer,
                                          final String claimName, final List<String> claimValue, final Algorithm algorithm) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .withClaim(claimName, claimValue)
                .sign(algorithm);
    }

    public static String buildRefreshToken(final String subject, final Date expiresAt,
                                           final String issuer, final Algorithm algorithm) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public static void sendErrors(final HttpServletResponse response, final int status, final String errorName,
                                  final String message, final String contentType) throws IOException {
        response.setStatus(status);
        final Map<String, String> error = new HashMap<>();
        error.put(errorName, message);
        response.setContentType(contentType);

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
