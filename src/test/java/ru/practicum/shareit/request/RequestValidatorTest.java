package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.RequestDto;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {

    @Mock
    javax.validation.Validator validator;

    @InjectMocks
    RequestValidator requestValidator;

    RequestDto requestDto = new RequestDto("text");

    @Test
    void validateAllFields() {
        requestValidator.validateAllFields(requestDto);

        verify(validator, times(1)).validate(requestDto);
    }

    @Test
    void validateNonNullFields() {
        requestValidator.validateNonNullFields(requestDto);

        verify(validator, never()).validate(requestDto);
    }
}