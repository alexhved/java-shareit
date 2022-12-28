package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemValidatorTest {

    @Mock
    private javax.validation.Validator validator;

    @InjectMocks
    private ItemValidator itemValidator;

    private static final String NAME = "name";
    private static final String AVAILABLE = "available";

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "banan", "yellow", true, null);

    @Test
    void validateAllFields_shouldWork() {
        itemValidator.validateAllFields(itemRequestDto);

        verify(validator, times(1)).validate(itemRequestDto);
    }

    @Test
    void validateAllFields_shouldThrown() {
        itemRequestDto.setDescription("");

        when(validator.validate(itemRequestDto)).thenThrow(ValidateException.class);

        assertThrows(ValidateException.class, () -> itemValidator.validateAllFields(itemRequestDto));

        verify(validator, times(1)).validate(itemRequestDto);
    }

    @Test
    void validateNonNullFields_shouldWork() {
        itemRequestDto.setDescription(null);

        itemValidator.validateNonNullFields(itemRequestDto);

        verify(validator, times(1)).validateProperty(itemRequestDto, NAME);
        verify(validator, times(1)).validateProperty(itemRequestDto, AVAILABLE);
    }
}