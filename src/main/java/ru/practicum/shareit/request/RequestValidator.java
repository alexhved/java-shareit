package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.utils.AbstractValidator;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestValidator extends AbstractValidator<RequestDto> {

    private static final String DESCRIPTION = "description";

    protected RequestValidator(Validator validator) {
        super(validator);
    }

    @Override
    protected void validateAllFields(RequestDto requestDto) {
        List<String> errors = getErrorsByAllFields(requestDto);
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

    @Override
    protected void validateNonNullFields(RequestDto requestDto) {
        List<String> errors = new ArrayList<>();
        if (requestDto.getDescription() != null) {
            errors.addAll(getErrorsByField(requestDto, DESCRIPTION));
        }
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }
}
