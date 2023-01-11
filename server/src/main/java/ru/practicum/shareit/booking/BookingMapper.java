package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserIdDto;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    private final ItemMapper itemMapper;

    public BookingMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public BookingResponseDto mapToResponseDto(Booking booking) {
        ItemResponseDto itemResponseDto = itemMapper.mapToItemResponseDto(booking.getItem());
        UserIdDto userIdDto = new UserIdDto(booking.getBooker().getId());

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemResponseDto)
                .booker(userIdDto)
                .status(booking.getStatus())
                .build();
    }

    public BookingResponseDto mapToResponseDto(Booking booking, User user, Item item) {
        ItemResponseDto itemResponseDto = itemMapper.mapToItemResponseDto(item);
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new UserIdDto(user.getId()))
                .item(itemResponseDto)
                .build();
    }

    public Booking mapToBooking(BookingRequestDto bookingRequestDto, User user, Item item) {
        return Booking.builder()
                .booker(user)
                .item(item)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status(bookingRequestDto.getStatus())
                .build();
    }
}
