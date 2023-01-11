package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                     @RequestBody BookingRequestDto bookingRequestDto) {

        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {

        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getByUserOrOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId) {

        return bookingService.getByBookerOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllByBookerIdAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                                              @RequestParam(required = false) Integer from,
                                                              @RequestParam(required = false) Integer size) {

        return bookingService.findAllByBookerIdAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllByOwnerIdAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                                             @RequestParam(required = false) Integer from,
                                                             @RequestParam(required = false) Integer size) {

        return bookingService.findAllByOwnerIdAndState(userId, state, from, size);
    }
}
