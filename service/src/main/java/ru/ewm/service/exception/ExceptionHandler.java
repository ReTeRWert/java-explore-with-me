package ru.ewm.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        Optional<FieldError> optionalFieldError = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst();

        String message = "";

        if (optionalFieldError.isPresent()) {

            FieldError fieldError = optionalFieldError.get();
            String fieldName = fieldError.getField();
            String value = Objects.requireNonNull(fieldError.getRejectedValue()).toString();
            String error = fieldError.getDefaultMessage();
            message = String.format("Field: %s, error: %s, value: %s", fieldName, error, value);
        }

        log.warn(message);

        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.BAD_REQUEST.name());
        errorApi.setReason("Invalid request");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());

        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApi handleConstraintViolationException(final DataIntegrityViolationException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.CONFLICT.name());
        errorApi.setReason("Integrity constraint violated");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApi handleEmptyResultDataAccessException(final NotFoundException e) {
        String message = String.format("%s with id=%s not found", e.getClassName(), e.getId());
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.NOT_FOUND.name());
        errorApi.setReason("Required object not found");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleEventNotFoundException(final EventNotFoundException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.BAD_REQUEST.name());
        errorApi.setReason("Required object not found");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.BAD_REQUEST.name());
        errorApi.setReason("Invalid request");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApi handleInvalidOperationException(final InvalidOperationException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.CONFLICT.name());
        errorApi.setReason("Invalid operation");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleInvalidEventDateException(final InvalidEventDateException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.BAD_REQUEST.name());
        errorApi.setReason("Invalid operation");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.BAD_REQUEST.name());
        errorApi.setReason("Invalid request");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApi handleThrowable(final Throwable e) {
        log.warn(e.getMessage());
        return null;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApi handleAccessException(final AccessException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.CONFLICT.name());
        errorApi.setReason("Access error");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleIllegalArgumentException(final IllegalArgumentException e) {
        String message = e.getMessage();
        log.warn(message);
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(HttpStatus.BAD_REQUEST.name());
        errorApi.setReason("Argument error");
        errorApi.setMessage(message);
        errorApi.setTimestamp(LocalDateTime.now());
        return errorApi;
    }
}
