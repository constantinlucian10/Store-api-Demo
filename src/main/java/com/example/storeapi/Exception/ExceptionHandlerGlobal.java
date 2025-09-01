package com.example.storeapi.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerGlobal {

    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlerGlobal.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.warn("Runtime error: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Runtime error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            sb.append(fieldError.getField())
                    .append(fieldError.getDefaultMessage())
                    .append(";");
        });

        String details = sb.length() > 0 ? sb.toString() : "Failed validation";

        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Failed validation", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptionSituation(Exception ex) {
        logger.error("Unexpected error !", ex);

        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Internal error !", ex.getMessage()));
    }

}
