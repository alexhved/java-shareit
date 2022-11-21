package ru.practicum.shareit.error;

import java.util.ArrayList;
import java.util.List;

public class ValidateException extends RuntimeException {
    private final List<String> errorMessages;
    public ValidateException(List<String> errorMessages) {
        this.errorMessages = new ArrayList<>();
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
