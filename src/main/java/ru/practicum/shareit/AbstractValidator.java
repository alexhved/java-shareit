package ru.practicum.shareit;

import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Validated
public class AbstractValidator<T> implements Validator<T> {
    protected final List<String> errorMessages = new ArrayList<>();
    protected final javax.validation.Validator validator;

    public AbstractValidator(javax.validation.Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validateAllFields(@Valid T t) {

    }

    @Override
    public void validateNonNullFields(T t) {

    }

    @Override
    public void validateField(T t, String fieldName) {
        validator.validateProperty(t, fieldName).stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .ifPresent(errorMessages::add);
    }
}
