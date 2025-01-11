package org.skillsrock.task.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import org.skillsrock.task.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ErrorDTO> handleInvalidFormatException(
      InvalidFormatException e, WebRequest request) {
    return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorDTO> handleHttpMessageNotReadableException(WebRequest request) {
    return createErrorResponse(
        HttpStatus.BAD_REQUEST, "Некорректный формат JSON", request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e, WebRequest request) {
    String errorMessage = e.getBindingResult().getFieldErrors()
        .stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .findFirst()
        .orElse("Ошибка валидации данных");
    return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleEntityNotFoundException(
      EntityNotFoundException e, WebRequest request) {
    return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
  }

  private ResponseEntity<ErrorDTO> createErrorResponse(
      HttpStatus status, String message, WebRequest request) {
    ErrorDTO errorResponse = new ErrorDTO(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getDescription(false)
    );
    return new ResponseEntity<>(errorResponse, status);
  }
}
