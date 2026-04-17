package com.enelrith.theelderforge.shared;

import com.enelrith.theelderforge.shared.exception.AlreadyExistsException;
import com.enelrith.theelderforge.shared.exception.NotFoundException;
import com.enelrith.theelderforge.shared.exception.NotValidException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.enelrith.theelderforge.shared.ErrorResponse.buildErrorResponse;
import static com.enelrith.theelderforge.shared.ErrorResponse.buildValidationErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage, (existing, duplicate) -> existing));
        var response = buildValidationErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), request.getServletPath(), errors);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(NotFoundException e, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var response = buildErrorResponse(Instant.now(), status.value(), e.getMessage(), status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(AlreadyExistsException e, HttpServletRequest request) {
        var status = HttpStatus.CONFLICT;
        var response = buildErrorResponse(Instant.now(), status.value(), e.getMessage(), status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(NotValidException e, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var response = buildErrorResponse(Instant.now(), status.value(), e.getMessage(), status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(BadCredentialsException e, HttpServletRequest request) {
        var status = HttpStatus.UNAUTHORIZED;
        var response = buildErrorResponse(Instant.now(), status.value(), "Invalid credentials", status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleException(AuthenticationException e, HttpServletRequest request) {
        var status = HttpStatus.UNAUTHORIZED;
        var response = buildErrorResponse(Instant.now(), status.value(), "Invalid credentials", status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessDeniedException e, HttpServletRequest request) {
        var status = HttpStatus.FORBIDDEN;
        var response = buildErrorResponse(Instant.now(), status.value(), "You are bit permitted to access to this resource", status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var response = buildErrorResponse(Instant.now(), status.value(), e.getMessage(), status.getReasonPhrase(), request.getServletPath());

        return ResponseEntity.status(status).body(response);
    }
}