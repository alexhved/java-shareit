package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ErrorHandler.class)
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    private final String error = "some message";

    @Test
    void validateListHandle() {
        List<ErrorResponse> errorMessage = errorHandler.validateListHandle(new ValidateException(List.of(error)));

        assertThat(errorMessage.get(0)).hasFieldOrPropertyWithValue("error", error);
    }

    @Test
    void resourceNotFoundHandle() {
        ErrorResponse errorResponse = errorHandler.resourceNotFoundHandle(new ResourceNotFoundException(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", error);
    }

    @Test
    void resourceAlreadyExistHandle() {
        ErrorResponse errorResponse = errorHandler.resourceAlreadyExistHandle(new ResourceAlreadyExistException(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", error);

    }

    @Test
    void resourceAccessException() {
        ErrorResponse errorResponse = errorHandler.resourceAccessException(new ResourceAccessException(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", error);
    }

    @Test
    void cataIntegrityExceptionHandler() {
        ErrorResponse errorResponse = errorHandler.dataIntegrityExceptionHandler(new DataIntegrityViolationException(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", "Insert data error");
    }

    @Test
    void illegalArgumentExceptionHandler() {
        ErrorResponse errorResponse = errorHandler.illegalArgumentExceptionHandler(new IllegalArgumentException(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", error);
    }

    @Test
    void handleThrowable() {
        ErrorResponse errorResponse = errorHandler.handleThrowable(new Throwable(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", "An unexpected error has occurred.");
    }

    @Test
    void bookingStatusExceptionHandler() {
        ErrorResponse errorResponse = errorHandler.bookingStatusExceptionHandler(new BookingStatusException(error));
        assertThat(errorResponse).hasFieldOrPropertyWithValue("error", error);
    }
}