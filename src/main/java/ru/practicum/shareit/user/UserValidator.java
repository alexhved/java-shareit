package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.AbstractValidator;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserValidator extends AbstractValidator<UserDto> {
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    @Autowired
    public UserValidator(Validator validator) {
        super(validator);
    }

    @Override
    public void validateAllFields(UserDto userDto) {
        List<String> errors = getErrorsByAllFields(userDto);
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

    @Override
    public void validateNonNullFields(UserDto userDto) {
        List<String> errors = new ArrayList<>();
        if (userDto.getName() != null) {
            errors.addAll(getErrorsByField(userDto, NAME));
        }
        if (userDto.getEmail() != null) {
            errors.addAll(getErrorsByField(userDto, EMAIL));
        }
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

}
