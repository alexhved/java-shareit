package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.AbstractValidator;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemValidatorImpl extends AbstractValidator<ItemDto> {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String AVAILABLE = "available";

    @Autowired
    public ItemValidatorImpl(Validator validator) {
        super(validator);
    }

    @Override
    public void validateAllFields(ItemDto itemDto) {
        List<String> errors = getErrorsByAllFields(itemDto);
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

    @Override
    public void validateNonNullFields(ItemDto itemDto) {
        List<String> errors = new ArrayList<>();
        if (itemDto.getName() != null) {
            errors.addAll(getErrorsByField(itemDto, NAME));
        }
        if (itemDto.getDescription() != null) {
            errors.addAll(getErrorsByField(itemDto, DESCRIPTION));
        }
        if (itemDto.getAvailable() != null) {
            errors.addAll(getErrorsByField(itemDto, AVAILABLE));
        }
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }
}
