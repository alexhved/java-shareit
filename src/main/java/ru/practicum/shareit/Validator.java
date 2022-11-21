package ru.practicum.shareit;

import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import javax.validation.Valid;

@Validated
public interface Validator<T> {
    void validateAllFields(@Valid T t);
    void validateNonNullFields(T t);
}
