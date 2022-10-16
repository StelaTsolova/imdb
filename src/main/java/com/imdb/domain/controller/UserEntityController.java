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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.util.*;

import static com.imdb.util.UtilClass.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserEntityController {

    private final UserEntityService userEntityService;

    @PostMapping(PATH_REGISTER)
    public ResponseEntity<?> register(@RequestBody @Valid final UserEntityRegisterDto userEntityRegisterDto,
                                      final BindingResult bindingResult) {
        log.info("Post request on path {}: email={}, firstName ={}, lastName={}", PATH_REGISTER,
                userEntityRegisterDto.getEmail(), userEntityRegisterDto.getFirstName(), userEntityRegisterDto.getLastName());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("Response from post request on path {}: errorMessages={}", PATH_REGISTER,
                    getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        userEntityService.registerUser(userEntityRegisterDto);

        log.info("Response from post request on path {}: status={}", PATH_REGISTER, "200");
        return ResponseEntity.ok().build();
    }

    @PostMapping(PATH_CONTROL)
    @PreAuthorize(value = "ADMIN")
    public ResponseEntity<?> changeRole(@RequestBody final RoleChangeDto roleChangeDto,
                                        final BindingResult bindingResult) {
        log.info("Post request on path {}: userEmail={}, role ={}", PATH_CONTROL,
                roleChangeDto.getUserEmail(), roleChangeDto.getRole());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("Response from post request on path {}: errorMessages={}", PATH_CONTROL,
                    getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        userEntityService.changeUserRole(roleChangeDto);

        log.info("Response from post request on path {}: status={}", PATH_CONTROL, "200");
        return ResponseEntity.ok().build();
    }

    @GetMapping(PATH_TOKEN_REFRESH)
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

                final String accessToken = buildAccessToken(user.getEmail(), expiresAt,
                        request.getRequestURL().toString(), "roles", roles, algorithm);

                final Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                sendErrors(response, FORBIDDEN.value(), "error_message",
                        exception.getMessage(), APPLICATION_JSON_VALUE);
            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler
    public @ResponseBody Map<String, String> IllegalArgumentExHandler() {
        return Map.of("error_message", "Role not exist.");
    }
}
