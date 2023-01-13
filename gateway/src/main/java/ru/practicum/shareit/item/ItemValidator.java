package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.utils.AbstractValidator;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemValidator extends AbstractValidator<ItemRequestDto> {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String AVAILABLE = "available";

    @Autowired
    public ItemValidator(Validator validator) {
        super(validator);
    }

    @Override
    public void validateAllFields(ItemRequestDto itemRequestDto) {
        List<String> errors = getErrorsByAllFields(itemRequestDto);
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

    @Override
    public void validateNonNullFields(ItemRequestDto itemRequestDto) {
        List<String> errors = new ArrayList<>();
        if (itemRequestDto.getName() != null) {
            errors.addAll(getErrorsByField(itemRequestDto, NAME));
        }
        if (itemRequestDto.getDescription() != null) {
            errors.addAll(getErrorsByField(itemRequestDto, DESCRIPTION));
        }
        if (itemRequestDto.getAvailable() != null) {
            errors.addAll(getErrorsByField(itemRequestDto, AVAILABLE));
        }
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }
}
