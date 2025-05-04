package com.reon.url_backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExceptions(EmailAlreadyExistsException emailAlreadyExistsException){
        logger.info("Email Exception: " + emailAlreadyExistsException.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "User with this email already exists.");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUsernameExceptions(UsernameAlreadyExistsException usernameAlreadyExistsException){
        logger.info("Email Exception: " + usernameAlreadyExistsException.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "User with this username already exists.");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RestrictionException.class)
    public ResponseEntity<Map<String, String>> handleRestrictionException(RestrictionException restrictionException){
        logger.info("Restriction Exception: " + restrictionException.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "This operation is not allowed.");
        return ResponseEntity.badRequest().body(error);
    }
}
