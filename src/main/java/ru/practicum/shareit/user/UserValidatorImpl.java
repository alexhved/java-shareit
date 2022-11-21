package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.AbstractValidator;
import ru.practicum.shareit.error.ResourceAlreadyExistException;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserValidatorImpl extends AbstractValidator<UserDto> {
    private final UserRepository userRepository;
    //private final javax.validation.Validator validator;
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    @Autowired
    public UserValidatorImpl(Validator validator, UserRepository userRepository /*Validator validator1*/) {
        super(validator);
        this.userRepository = userRepository;
        //this.validator = validator1;
    }

    @Override
    public void validateAllFields(@Valid UserDto userDto) {
        checkEmailOnDuplicate(userDto.getEmail());
    }

    @Override
    public void validateNonNullFields(UserDto userDto) {
        if (userDto.getName() != null) {
            validateField(userDto, NAME);
        }
        if (userDto.getEmail() != null) {
            validateField(userDto, EMAIL);
        }
        if (!errorMessages.isEmpty()) {
            throw new ValidateException(errorMessages);
        }
        if (userDto.getEmail() != null) {
            checkEmailOnDuplicate(userDto.getEmail());
        }
    }

    private void checkEmailOnDuplicate(String email) {
        List<User> users = userRepository.findAll();
        Optional<String> optEmail = users.stream()
                .map(User::getEmail)
                .filter(otherEmail -> otherEmail.equals(email))
                .findFirst();

        if (optEmail.isPresent() && optEmail.get().equals(email)) {
            throw new ResourceAlreadyExistException(String.format("email: %s already exist", email));
        }
    }

    @Override
    public void validateField(UserDto userDto, String fieldName) {
        validator.validateProperty(userDto, fieldName).stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .ifPresent(errorMessages::add);
    }
}
