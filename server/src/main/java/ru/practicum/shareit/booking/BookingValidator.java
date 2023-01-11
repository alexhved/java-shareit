package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.utils.AbstractValidator;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BookingValidator extends AbstractValidator<BookingRequestDto> {
    private static final String START = "start";
    private static final String END = "end";
    private static final String ITEM_ID = "itemId";
    private static final String BOOKER_ID = "bookerId";

    protected BookingValidator(Validator validator) {
        super(validator);
    }

    @Override
    protected void validateAllFields(BookingRequestDto bookingRequestDto) {
        validator.validate(bookingRequestDto);
    }

    @Override
    protected void validateNonNullFields(BookingRequestDto bookingRequestDto) {
        List<String> errors = new ArrayList<>();
        if (bookingRequestDto.getStart() != null) {
            errors.addAll(getErrorsByField(bookingRequestDto, START));
        }
        if (bookingRequestDto.getEnd() != null) {
            errors.addAll(getErrorsByField(bookingRequestDto, END));
            errors.addAll(checkEndTime(bookingRequestDto));
        }
        if (bookingRequestDto.getItemId() != null) {
            errors.addAll(getErrorsByField(bookingRequestDto, ITEM_ID));
        }
        if (bookingRequestDto.getBookerId() != null) {
            errors.addAll(getErrorsByField(bookingRequestDto, BOOKER_ID));
        }
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }
    }

    private List<String> checkEndTime(BookingRequestDto bookingRequestDto) {
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        if (end.isBefore(start)) {
            return List.of("end time cannot be early start time");
        } else {
            return Collections.emptyList();
        }
    }
}
