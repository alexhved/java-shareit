package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {
    ItemDto save(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto findById(long userId, long itemId);

    ItemDto findById(long itemId);

    List<Item> findAllByUserId(long userId);

    List<ItemDto> searchItem(long userId, String text);

}
