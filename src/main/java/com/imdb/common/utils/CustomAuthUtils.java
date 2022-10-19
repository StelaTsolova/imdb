package com.imdb.common.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

public class CustomAuthUtils {

    public static DecodedJWT getDecodedJWT(String authorizationHeader, String password) {
        final String token = authorizationHeader.substring("Bearer ".length());
        final Algorithm algorithm = HMAC256(password.getBytes());
        final JWTVerifier verifier = require(algorithm).build();

        return verifier.verify(token);
    }
}
