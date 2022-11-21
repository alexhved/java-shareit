package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.Validator;
import ru.practicum.shareit.error.ResourceAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class UserValidatorImpl implements Validator<UserDto> {
    private final UserRepository userRepository;
    private final javax.validation.Validator validator;
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    @Override
    public void validateAllFields(@Valid UserDto userDto) {
        Optional<String> optEmail = findEmail(userDto.getEmail());
        if (optEmail.isPresent()) {
            throw new ResourceAlreadyExistException(String.format("email: %s already exist", userDto.getEmail()));
        }
    }

    @Override
    public void validateNonNullFields(UserDto userDto) {
        if (userDto.getName() != null) {
            Optional<String> optErrorMessage = validator.validateProperty(userDto, NAME).stream()
                    .map(ConstraintViolation::getMessage).findFirst();
            if (optErrorMessage.isPresent()) {
                throw new ValidationException(optErrorMessage.get());
            }
        }
        if (userDto.getEmail() != null) {
            Optional<String> optErrorMessage = validator.validateProperty(userDto, EMAIL).stream()
                    .map(ConstraintViolation::getMessage).findFirst();
            if (optErrorMessage.isPresent()) {
                throw new ValidationException(optErrorMessage.get());
            }
            Optional<String> optEmail = findEmail(userDto.getEmail());
            if (optEmail.isPresent() && optEmail.get().equals(userDto.getEmail())) {
                throw new ResourceAlreadyExistException(String.format("email: %s already exist", userDto.getEmail()));
            }
        }
    }

    private Optional<String> findEmail(String email) {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(User::getEmail)
                .filter(otherEmail -> otherEmail.equals(email))
                .findFirst();
    }
}
