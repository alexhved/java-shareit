package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.utils.AbstractValidator;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentValidator extends AbstractValidator<CommentRequestDto> {

    private static final String TEXT = "text";

    public CommentValidator(Validator validator) {
        super(validator);
    }

    @Override
    protected void validateAllFields(CommentRequestDto commentRequestDto) {
        List<String> errors = getErrorsByAllFields(commentRequestDto);
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

    @Override
    protected void validateNonNullFields(CommentRequestDto commentRequestDto) {
        List<String> errors = new ArrayList<>(getErrorsByField(commentRequestDto, TEXT));
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }
}
