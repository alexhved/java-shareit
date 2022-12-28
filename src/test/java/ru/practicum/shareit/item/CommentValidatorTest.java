package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Mock
    private javax.validation.Validator validator;

    @InjectMocks
    private CommentValidator commentValidator;

    private final CommentRequestDto commentRequestDto = new CommentRequestDto("text");

    @Test
    void validateAllFields() {
        commentValidator.validateAllFields(commentRequestDto);
        verify(validator, times(1)).validate(commentRequestDto);
    }

    @Test
    void validateNonNullFields() {
        commentValidator.validateNonNullFields(commentRequestDto);
        verify(validator, times(1)).validateProperty(commentRequestDto, "text");
    }
}