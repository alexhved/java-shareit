package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public interface ItemService {
    ItemDto save(long userId, ItemDto itemDto);
}
