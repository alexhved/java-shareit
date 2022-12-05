package ru.practicum.shareit.item;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@Primary
public class ItemJpaServiceImpl implements ItemService {
    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        return null;
    }

    @Override
    public ItemDto findById(long itemId) {
        return null;
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return null;
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        return null;
    }
}
