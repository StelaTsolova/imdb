package com.imdb.common.utils.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

public class JWTDecoderUtils {

    public static DecodedJWTInfo decodeJWT(final String authorizationHeader) {
        final String token = authorizationHeader.substring("Bearer ".length());
        final Algorithm algorithm = HMAC256("pass".getBytes());
        final JWTVerifier verifier = require(algorithm).build();
        final DecodedJWT decodedJWT = verifier.verify(token);

        return DecodedJWTInfo.builder()
                .token(token)
                .algorithm(algorithm)
                .decodedJWT(decodedJWT)
                .build();
    }
}
