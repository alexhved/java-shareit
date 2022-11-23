package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


@Service
public class ItemMapperImpl implements ItemMapper {
    @Override
    public Item mapToItem(ItemDto dto) {
        return new Item(dto.getId(),
                0,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable());
    }

    @Override
    public Item mapToItem(ItemDto dto, Item item, long userId, long itemId) {
        return Item.builder()
                .id(itemId)
                .userId(userId)
                .name(dto.getName() != null ? dto.getName() : item.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : item.getDescription())
                .available(dto.getAvailable() != null ? dto.getAvailable() : item.getAvailable())
                .build();
    }

    @Override
    public ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }
}
