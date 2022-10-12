package com.example.imdb.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    public static Map<String, String> getErrorMessages(List<ObjectError> allErrors) {
        Map<String, String> errorMessages = new HashMap<>();

        for (Object object : allErrors) {
            if (object instanceof FieldError fieldError) {
                errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());

                log.info("Not valid filed {}, reason is {}", fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return errorMessages;
    }

    public static String buildAccessToken(String subject, Date expiresAt, String issuer,
                                          String claimName, List<String> claimValue, Algorithm algorithm) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .withClaim(claimName, claimValue)
                .sign(algorithm);
    }

    public static String buildRefreshToken(String subject, Date expiresAt, String issuer, Algorithm algorithm) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public static void sendErrors(HttpServletResponse response, int status, String errorName,
                                  String message, String contentType) throws IOException {
        response.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put(errorName, message);
        response.setContentType(contentType);

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
