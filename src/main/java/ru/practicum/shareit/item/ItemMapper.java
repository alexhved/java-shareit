package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    Item mapToItem(ItemDto dto);

    Item mapToItem(ItemDto dto, Item item, long userId, long itemId);

    ItemDto mapToItemDto(Item item);
}
