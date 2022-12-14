package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> validateListHandle(final ValidateException e) {
        List<String> errorMessages = e.getErrorMessages();
        errorMessages.forEach(log::warn);

        return errorMessages.stream()
                .map(ErrorResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse resourceNotFoundHandle(final ResourceNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse resourceAlreadyExistHandle(final ResourceAlreadyExistException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse resourceAccessException(final ResourceAccessException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> constraintViolationHandler(final ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .map(message -> {
                    log.warn(message);
                    return new ErrorResponse(message);
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse cataIntegrityExceptionHandler(final DataIntegrityViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Insert data error");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse requestHeaderExceptionHandler(final MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentExceptionHandler(final IllegalArgumentException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(e.getMessage());
        return new ErrorResponse("An unexpected error has occurred.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse bookingStatusExceptionHandler(final BookingStatusException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
