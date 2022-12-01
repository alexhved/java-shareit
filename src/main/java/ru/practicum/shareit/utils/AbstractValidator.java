package ru.practicum.shareit.utils;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractValidator<T> {
    protected final javax.validation.Validator validator;

    protected AbstractValidator(javax.validation.Validator validator) {
        this.validator = validator;
    }

    protected abstract void validateAllFields(T t);

    protected abstract void validateNonNullFields(T t);

    protected List<String> getErrorsByField(T t, String fieldName) {
        return validator.validateProperty(t, fieldName).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toUnmodifiableList());
    }

    protected List<String> getErrorsByAllFields(T t) {
        return validator.validate(t).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toUnmodifiableList());
    }
}
