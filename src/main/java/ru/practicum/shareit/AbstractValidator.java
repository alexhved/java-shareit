package ru.practicum.shareit;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractValidator<T> implements Validator<T> {
    protected final javax.validation.Validator validator;

    public AbstractValidator(javax.validation.Validator validator) {
        this.validator = validator;
    }

    @Override
    public abstract void validateAllFields(T t);

    @Override
    public abstract void validateNonNullFields(T t);

    @Override
    public List<String> getErrorsByField(T t, String fieldName) {
        return validator.validateProperty(t, fieldName).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<String> getErrorsByAllFields(T t) {
        return validator.validate(t).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toUnmodifiableList());
    }
}
