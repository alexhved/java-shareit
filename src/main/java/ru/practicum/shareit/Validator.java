package ru.practicum.shareit;

import java.util.List;

public interface Validator<T> {
    void validateAllFields(T t);
    void validateNonNullFields(T t);

    List<String> getErrorsByField(T t, String fieldName);

    List<String> getErrorsByAllFields(T t);
}
