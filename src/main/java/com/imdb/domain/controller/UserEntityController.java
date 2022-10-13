package com.imdb.domain.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imdb.domain.model.dto.RoleChangeDto;
import com.imdb.domain.model.dto.UserEntityRegisterDto;
import com.imdb.domain.model.entity.UserEntity;
import com.imdb.domain.service.UserEntityService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imdb.util.UtilClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserEntityController {

    private final UserEntityService userEntityService;

    @PostMapping(UtilClass.PATH_REGISTER)
    public ResponseEntity<?> register(@RequestBody @Valid final UserEntityRegisterDto userEntityRegisterDto,
                                      final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(UtilClass.getErrorMessages(bindingResult.getAllErrors()));
        }

        userEntityService.registerUser(userEntityRegisterDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping(UtilClass.PATH_CONTROL)
    @PreAuthorize(value = "ADMIN")
    public ResponseEntity<?> changeRole(@RequestBody final RoleChangeDto roleChangeDto,
                                        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(UtilClass.getErrorMessages(bindingResult.getAllErrors()));
        }

        this.userEntityService.changeUserRole(roleChangeDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping(UtilClass.PATH_TOKEN_REFRESH)
    public void refreshToken(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                final String refreshToken = authorizationHeader.substring("Bearer ".length());
                final Algorithm algorithm = Algorithm.HMAC256("pass".getBytes());
                final JWTVerifier verifier = JWT.require(algorithm).build();
                final DecodedJWT decodedJWT = verifier.verify(refreshToken);

                final String email = decodedJWT.getSubject();

                final UserEntity user = userEntityService.getUserEntityByEmail(email);
                final List<String> roles = List.of(user.getRole().name());
                final Date expiresAt = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                final String accessToken = UtilClass.buildAccessToken(user.getEmail(), expiresAt,
                        request.getRequestURL().toString(), "roles", roles, algorithm);

                final Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                UtilClass.sendErrors(response, FORBIDDEN.value(), "error_message",
                        exception.getMessage(), APPLICATION_JSON_VALUE);
            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public @ResponseBody Map<String, String> IllegalArgumentExHandler(IllegalArgumentException exception){
        log.info("IllegalArgumentExHandler catch exception with message {}", exception.getMessage());

        return Map.of("error_message", "Role not exist.");
    }
}
