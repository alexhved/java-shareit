package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

    @Mock
    javax.validation.Validator validator;

    @InjectMocks
    BookingValidator bookingValidator;

    private static final String START = "start";
    private static final String END = "end";
    private static final String ITEM_ID = "itemId";
    private static final String BOOKER_ID = "bookerId";

    BookingRequestDto bookingRequestDto =
            new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, Status.WAITING);

    @Test
    void validateAllFields() {
        bookingValidator.validateAllFields(bookingRequestDto);
        verify(validator, times(1)).validate(bookingRequestDto);
    }

    @Test
    void validateNonNullFields() {
        bookingRequestDto.setStatus(null);
        bookingValidator.validateNonNullFields(bookingRequestDto);

        verify(validator, times(1)).validateProperty(bookingRequestDto, START);
        verify(validator, times(1)).validateProperty(bookingRequestDto, END);
        verify(validator, times(1)).validateProperty(bookingRequestDto, ITEM_ID);
        verify(validator, times(1)).validateProperty(bookingRequestDto, BOOKER_ID);
    }
}