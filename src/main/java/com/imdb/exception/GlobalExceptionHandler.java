package com.imdb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ObjectNotFoundException.class})
    public @ResponseBody Map<String,String> handlerErrors(RuntimeException exception){
        return Map.of("error_message", exception.getMessage());
    }
}
