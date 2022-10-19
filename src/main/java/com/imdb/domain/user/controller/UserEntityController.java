package com.imdb.domain.user.controller;

import com.imdb.common.utils.jwt.DecodedJWTInfo;
import com.imdb.domain.user.model.dto.role.RoleChangeDTO;
import com.imdb.domain.user.model.dto.UserEntityRegisterDTO;
import com.imdb.domain.user.model.entity.User;
import com.imdb.domain.user.service.UserEntityService;

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
import java.time.LocalDate;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.imdb.common.Constants.*;
import static com.imdb.common.utils.ErrorUtils.getErrorMessages;
import static com.imdb.common.utils.ErrorUtils.getErrorMessagesToString;
import static com.imdb.common.utils.TokenUtils.buildAccessToken;
import static com.imdb.common.utils.TokenUtils.sendErrors;
import static com.imdb.common.utils.jwt.JWTDecoderUtils.decodeJWT;
import static java.time.LocalDate.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserEntityController {

    private final UserEntityService userEntityService;
    private final ObjectMapper objectMapper;

    @PostMapping(PATH_REGISTER)
    public ResponseEntity<?> register(@RequestBody @Valid final UserEntityRegisterDTO userEntityRegisterDto,
                                      final BindingResult bindingResult) {
        log.info("POST register request: email={}, firstName ={}, lastName={}", userEntityRegisterDto.getEmail(),
                userEntityRegisterDto.getFirstName(), userEntityRegisterDto.getLastName());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("POST register response: errorMessages={}", getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        userEntityService.registerUser(userEntityRegisterDto);

        log.info("POST register response: status={}", "200");
        return ResponseEntity.ok().build();
    }

    @PostMapping(PATH_CONTROL)
    @PreAuthorize(value = "ADMIN")
    public ResponseEntity<?> changeRole(@RequestBody @Valid final RoleChangeDTO roleChangeDto,
                                        final BindingResult bindingResult) {
        log.info("POST control request: userEmail={}, role ={}", roleChangeDto.getUserEmail(), roleChangeDto.getRole());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("POST control response: errorMessages={}", getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        userEntityService.changeUserRole(roleChangeDto);

        log.info("POST control response: status={}", "200");
        return ResponseEntity.ok().build();
    }

    @GetMapping(PATH_TOKEN_REFRESH)
    public void refreshToken(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                final DecodedJWTInfo info = decodeJWT(authorizationHeader);

                final String email = info.getDecodedJWT().getSubject();

                final User user = userEntityService.getUserEntityByEmail(email);
                final List<String> roles = List.of(user.getRole().name());
                final LocalDate expiresAt =now().plusDays(3);

                final String accessToken = buildAccessToken(user.getEmail(), expiresAt,
                        request.getRequestURL().toString(), "roles", roles, info.getAlgorithm());

                final Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", info.getToken());

                response.setContentType(APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(), tokens);

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
