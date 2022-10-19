package com.imdb.common.utils.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DecodedJWTInfo {

    private String token;

    private Algorithm algorithm;

    private DecodedJWT decodedJWT;
}
