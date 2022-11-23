package ru.practicum.shareit.error;

import java.util.List;

public class ValidateException extends RuntimeException {
    private final List<String> errorMessages;
    public ValidateException(List<String> errorMessages) {
        super();
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
