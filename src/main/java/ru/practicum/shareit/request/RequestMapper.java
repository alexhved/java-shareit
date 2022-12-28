package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface RequestMapper {
    ItemRequest mapToItemRequest(RequestDto requestDto);

    ResponseDto mapToResponseDto(ItemRequest itemRequest);
}
