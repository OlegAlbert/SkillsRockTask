package org.skillsrock.task.exception;

import static org.skillsrock.task.exception.ExceptionMessage.INVALID_JSON_FORMAT_EXCEPTION_MESSAGE;
import static org.skillsrock.task.exception.ExceptionMessage.VALIDATION_EXCEPTION_MESSAGE;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.LocalDateTime;
import org.skillsrock.task.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ErrorDTO> handleInvalidFormatException(
      InvalidFormatException e, WebRequest request) {
    e.getValue();
    return createErrorResponse(
        HttpStatus.BAD_REQUEST, INVALID_JSON_FORMAT_EXCEPTION_MESSAGE, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e, WebRequest request) {
    String errorMessage = e.getBindingResult().getFieldErrors()
        .stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .findFirst()
        .orElse(VALIDATION_EXCEPTION_MESSAGE);
    return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
  }

  @ExceptionHandler(WalletNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleWalletNotFoundException(
      WalletNotFoundException e, WebRequest request) {
    return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
  }

  @ExceptionHandler(NotEnoughMoneyException.class)
  public ResponseEntity<ErrorDTO> handleNotEnoughMoneyException(
      NotEnoughMoneyException e, WebRequest request) {
    return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
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
