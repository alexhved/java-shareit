package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    javax.validation.Validator validator;

    @InjectMocks
    UserValidator userValidator;

    private static final String NAME = "name";
    private static final String EMAIL = "email";

    UserDto userDto = new UserDto(1, "Ronny", "ggg@mail.com");

    @Test
    void validateAllFields_shouldWork() {
        userValidator.validateAllFields(userDto);

        verify(validator, Mockito.times(1)).validate(userDto);
    }

    @Test
    void validateAllFields_shouldThrow() {
        userDto.setEmail("incorrect");

        when(validator.validate(userDto)).thenThrow(ValidateException.class);

        assertThrows(ValidateException.class, () -> userValidator.validateAllFields(userDto));
    }

    @Test
    void validateNonNullFields_shouldWork() {

        userValidator.validateNonNullFields(userDto);

        verify(validator, Mockito.times(1)).validateProperty(userDto, NAME);
        verify(validator, times(1)).validateProperty(userDto, EMAIL);
    }

    @Test
    void validateNonNullFields_shouldThrow() {

        when(validator.validateProperty(userDto, NAME)).thenThrow(ValidateException.class);

        assertThrows(ValidateException.class, () -> userValidator.validateNonNullFields(userDto));
    }
}