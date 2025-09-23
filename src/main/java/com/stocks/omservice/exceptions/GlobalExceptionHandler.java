package com.stocks.omservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException resourceNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceNotFoundException.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegaleState(IllegalStateException stateException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(stateException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException exp){
        String errorMessage = exp.getBindingResult().getFieldErrors()
                .stream().map(ex -> ex.getField() + ":" + ex.getDefaultMessage())
                .collect(Collectors.joining());
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
