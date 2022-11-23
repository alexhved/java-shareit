package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> validationListHandle(final ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        violations.forEach(violation -> log.warn(violation.getMessage()));

        return violations.stream()
                .map(violation -> new ErrorResponse(violation.getMessage()))
                .collect(Collectors.toUnmodifiableList());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse ResourceNotFoundHandle(final ResourceNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse ResourceAlreadyExistHandle(final ResourceAlreadyExistException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(e.getMessage());
        return new ErrorResponse("An unexpected error has occurred.");
    }
}
