package com.imdb.common.utils;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ErrorUtils {
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
}
