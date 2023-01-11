package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


@Service
public class ItemMapper {

    public Item mapToItem(ItemRequestDto dto, User user, Optional<ItemRequest> itemRequest) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(user)
                .request(itemRequest.orElse(null))
                .build();
    }

    public Item mapToItem(ItemRequestDto dto, Item item, User user) {
        return Item.builder()
                .id(item.getId())
                .owner(user)
                .name(dto.getName() != null ? dto.getName() : item.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : item.getDescription())
                .available(dto.getAvailable() != null ? dto.getAvailable() : item.getAvailable())
                .build();
    }

    public ItemResponseDto mapToItemResponseDto(Item item) {
        ItemRequest request = item.getRequest();
        if (request != null) {
            return ItemResponseDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .requestId(request.getId())
                    .build();
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemResponseDto mapToItemResponseDto(Item item, List<CommentResponseDto> comments) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public ItemResponseDto mapToItemResponseDto(Item item, Booking last, Booking next) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(new ShortBooking(last.getId(), last.getBooker().getId()))
                .nextBooking(new ShortBooking(next.getId(), next.getBooker().getId()))
                .build();
    }
}
