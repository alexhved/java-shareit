package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.AbstractValidator;
import ru.practicum.shareit.error.ResourceAlreadyExistException;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserValidatorImpl extends AbstractValidator<UserDto> {
    private final UserMemRepository userMemRepository;
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    @Autowired
    public UserValidatorImpl(Validator validator, UserMemRepository userMemRepository) {
        super(validator);
        this.userMemRepository = userMemRepository;
    }

    @Override
    public void validateAllFields(UserDto userDto) {
        List<String> errors = getErrorsByAllFields(userDto);
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
        checkEmailOnDuplicate(userDto.getEmail());
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
        if (userDto.getEmail() != null) {
            checkEmailOnDuplicate(userDto.getEmail());
        }
    }

    private void checkEmailOnDuplicate(String email) {
        List<User> users = userMemRepository.findAll();
        Optional<String> optEmail = users.stream()
                .map(User::getEmail)
                .filter(otherEmail -> otherEmail.equals(email))
                .findFirst();

        if (optEmail.isPresent() && optEmail.get().equals(email)) {
            throw new ResourceAlreadyExistException(String.format("email: %s already exist", email));
        }
    }
}
