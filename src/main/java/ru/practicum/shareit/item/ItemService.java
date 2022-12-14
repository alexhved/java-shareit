package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Service
public interface ItemService {
    ItemResponseDto save(long userId, ItemRequestDto itemRequestDto);

    ItemResponseDto update(long userId, long itemId, ItemRequestDto itemRequestDto);

    ItemResponseDto findById(long userId, long itemId);

    List<ItemResponseDto> findAllByUserId(long userId);

    List<ItemResponseDto> searchItem(long userId, String text);

    CommentResponseDto addComment(long userId, long itemId, CommentRequestDto commentRequestDto);
}
