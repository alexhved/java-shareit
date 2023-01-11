package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingRequestDto {
    @Min(value = 1, message = "wrong id")
    private Long id;
    @FutureOrPresent(message = "start time must be future or present")
    private LocalDateTime start;
    @Future(message = "end time must be in the future")
    private LocalDateTime end;
    @Min(value = 1, message = "wrong item id")
    private Long itemId;
    @Min(value = 1, message = "wrong booker id")
    private Long bookerId;
    @NotBlank(message = "status not valid")
    private Status status;
}
