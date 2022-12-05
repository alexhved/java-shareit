package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMemServiceImpl implements ItemService {
    private final ItemMemRepository itemMemRepository;
    private final UserMemRepository userMemRepository;
    private final ItemMapper itemMapper;
    private final ItemValidatorImpl itemValidator;

    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        userMemRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        itemValidator.validateAllFields(itemDto);

        Item newItem = itemMapper.mapToItem(itemDto);
        newItem.setUserId(userId);
        Item savedItem = itemMemRepository.save(newItem);

        return itemMapper.mapToItemDto(savedItem);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userMemRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        Item itemById = itemMemRepository.findById(userId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        itemValidator.validateNonNullFields(itemDto);

        Item item = itemMapper.mapToItem(itemDto, itemById, userId, itemId);
        Item updatedItem = itemMemRepository.update(item);

        return itemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        Item item = itemMemRepository.findById(userId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto findById(long itemId) {
        Item item = itemMemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        userMemRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        return itemMemRepository.findAllByUserId(userId);
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        userMemRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        List<Item> result = itemMemRepository.search(text);

        return result.stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
