package ru.practicum.shareit.error;

public class ResourceAlreadyExistException extends RuntimeException {
    private String message;

    public ResourceAlreadyExistException(String message) {
        super(message);
    }
}
