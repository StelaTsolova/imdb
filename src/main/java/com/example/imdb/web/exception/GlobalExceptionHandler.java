package com.example.imdb.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ObjectNotFoundException.class})
    public @ResponseBody Map<String,String> handlerErrors(RuntimeException exception){
        log.info("GlobalExceptionHandler catch exception with message {}", exception.getMessage());

        return Map.of("error_message", exception.getMessage());
    }
}
