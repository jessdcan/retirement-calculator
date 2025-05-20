package com.example.retirementCalculator.exception;

import com.example.retirementCalculator.api.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the retirement calculator API.
 * <p>
 * Provides centralized exception handling across all controller methods.
 * Maps exceptions to appropriate HTTP status codes and standardized error response formats.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles validation exceptions thrown during request body validation.
     * <p>
     * Maps {@link MethodArgumentNotValidException} to HTTP 400 (Bad Request) responses
     * with detailed field-level validation errors.
     * </p>
     *
     * @param ex The validation exception
     * @param headers The HTTP headers for the response
     * @param status The selected response status
     * @param request The current request
     * @return A {@link ResponseEntity} with a detailed error response
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.error("Validation error: {}", ex.getMessage());

        List<ErrorResponseDTO.FieldErrorDto> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                    String message = error.getDefaultMessage();
                    Object rejectedValue = error instanceof FieldError ? ((FieldError) error).getRejectedValue() : null;

                    return ErrorResponseDTO.FieldErrorDto.builder()
                            .field(fieldName)
                            .message(message)
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .collect(Collectors.toList());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Request validation failed")
                .path(request.getDescription(false).replace("uri=", ""))
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link LifestyleNotFoundException}.
     * <p>
     * Maps to HTTP 404 (Not Found) responses when a requested lifestyle type is not found.
     * </p>
     *
     * @param ex The exception
     * @param request The current request
     * @return A {@link ResponseEntity} with an appropriate error response
     */
    @ExceptionHandler(LifestyleNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleLifestyleNotFoundException(
            LifestyleNotFoundException ex, WebRequest request) {

        log.error("Lifestyle not found: {}", ex.getMessage());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link InvalidParameterException}.
     * <p>
     * Maps to HTTP 400 (Bad Request) responses when input parameters are logically invalid.
     * </p>
     *
     * @param ex The exception
     * @param request The current request
     * @return A {@link ResponseEntity} with an appropriate error response
     */
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidParameterException(
            InvalidParameterException ex, WebRequest request) {

        log.error("Invalid parameter: {}", ex.getMessage());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Parameters")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link CalculationException}.
     * <p>
     * Maps to HTTP 500 (Internal Server Error) responses when a calculation error occurs.
     * </p>
     *
     * @param ex The exception
     * @param request The current request
     * @return A {@link ResponseEntity} with an appropriate error response
     */
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponseDTO> handleCalculationException(
            CalculationException ex, WebRequest request) {

        log.error("Calculation error: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Calculation Error")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@link CacheException}.
     * <p>
     * Maps to HTTP 503 (Service Unavailable) responses when the cache is unavailable.
     * </p>
     *
     * @param ex The exception
     * @param request The current request
     * @return A {@link ResponseEntity} with an appropriate error response
     */
    @ExceptionHandler(CacheException.class)
    public ResponseEntity<ErrorResponseDTO> handleCacheException(
            CacheException ex, WebRequest request) {

        log.error("Cache error: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Service Unavailable")
                .message("Cache service is currently unavailable. Please try again later.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

//    /**
//     * Handles {@link ConstraintViolationException}.
//     * <p>
//     * Maps to HTTP 400 (Bad Request) responses when constraint violations occur.
//     * </p>
//     *
//     * @param ex The exception
//     * @param request The current request
//     * @return A {@link ResponseEntity} with an appropriate error response
//     */
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
//            ConstraintViolationException ex, WebRequest request) {
//
//        log.error("Constraint violation: {}", ex.getMessage());
//
//        List<ErrorResponseDTO.FieldErrorDto> fieldErrors = new ArrayList<>();
//        ex.getConstraintViolations().forEach(violation -> {
//            String propertyPath = violation.getPropertyPath().toString();
//            String fieldName = propertyPath.contains(".") ?
//                    propertyPath.substring(propertyPath.lastIndexOf('.') + 1) : propertyPath;
//
//            fieldErrors.add(ErrorResponseDTO.FieldErrorDto.builder()
//                    .field(fieldName)
//                    .message(violation.getMessage())
//                    .rejectedValue(violation.getInvalidValue())
//                    .build());
//        });
//
//        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error("Constraint Violation")
//                .message("Validation constraints violated")
//                .path(request.getDescription(false).replace("uri=", ""))
//                .fieldErrors(fieldErrors)
//                .build();
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    /**
     * Fallback handler for all other exceptions.
     * <p>
     * Maps to HTTP 500 (Internal Server Error) responses for unhandled exceptions.
     * </p>
     *
     * @param ex The exception
     * @param request The current request
     * @return A {@link ResponseEntity} with a generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception: ", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}