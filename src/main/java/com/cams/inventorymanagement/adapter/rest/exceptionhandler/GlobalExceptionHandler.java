package com.cams.inventorymanagement.adapter.rest.exceptionhandler;

import static com.cams.inventorymanagement.adapter.rest.Constant.MDC_REQUEST_ID_KEY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.cams.inventorymanagement.adapter.rest.dto.response.ErrorResponse;
import com.cams.inventorymanagement.application.exceptions.DuplicateSkuException;
import com.cams.inventorymanagement.application.exceptions.RecordNotFoundException;
import com.cams.inventorymanagement.domain.exceptions.DomainValidationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String RESOURCE_NOT_FOUND = "The requested resource was not found";
  private static final String VALIDATION_FAILED = "Validation failed";
  private static final String DATA_IS_OUTDATED = "Data is outdated. Please try again.";

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
      NoResourceFoundException ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, NOT_FOUND, RESOURCE_NOT_FOUND);
  }

  @ExceptionHandler(RecordNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleRecordNotFoundException(
      RecordNotFoundException ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, NOT_FOUND);
  }

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ErrorResponse> handleDomainValidationExceptions(
      DomainValidationException ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, BAD_REQUEST);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex, HttpServletRequest request) {
    String errorMessage =
        ex.getParameterValidationResults().stream()
            .flatMap(
                result ->
                    result.getResolvableErrors().stream()
                        .map(
                            err -> {
                              String field = result.getMethodParameter().getParameterName();
                              return (field != null)
                                  ? field
                                  : "unknown" + ": " + err.getDefaultMessage();
                            }))
            .collect(Collectors.joining(", "));
    return getResponseEntity(ex, request, BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .findFirst()
            .orElse(VALIDATION_FAILED);
    return getResponseEntity(ex, request, BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorResponse> handleInvalidRequestException(
      Exception ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, BAD_REQUEST);
  }

  @ExceptionHandler({DuplicateSkuException.class})
  public ResponseEntity<ErrorResponse> handleDuplicateSkuException(
      DuplicateSkuException ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, CONFLICT);
  }

  @ExceptionHandler({
    ObjectOptimisticLockingFailureException.class,
    OptimisticLockingFailureException.class
  })
  public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(
      Exception ex, HttpServletRequest request) {
    return getResponseEntity(ex, request, CONFLICT, DATA_IS_OUTDATED);
  }

  private static ResponseEntity<ErrorResponse> getResponseEntity(
      Exception ex, HttpServletRequest request, HttpStatus status, String errorMessage) {
    log.error(errorMessage, ex);

    ErrorResponse errorResponse =
        new ErrorResponse(
            errorMessage,
            request.getRequestURI(),
            status.getReasonPhrase(),
            MDC.get(MDC_REQUEST_ID_KEY));

    return ResponseEntity.status(status).body(errorResponse);
  }

  private static ResponseEntity<ErrorResponse> getResponseEntity(
      Exception ex, HttpServletRequest request, HttpStatus status) {
    log.error(ex.getMessage(), ex);

    ErrorResponse errorResponse =
        new ErrorResponse(
            ex.getMessage(),
            request.getRequestURI(),
            status.getReasonPhrase(),
            MDC.get(MDC_REQUEST_ID_KEY));

    return ResponseEntity.status(status).body(errorResponse);
  }
}
