package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final ItemValidatorImpl itemValidator;

    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        itemValidator.validateAllFields(itemDto);

        Item newItem = itemMapper.mapToItem(itemDto);
        newItem.setUserId(userId);
        Item savedItem = itemRepository.save(newItem);

        return itemMapper.mapToItemDto(savedItem);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        Item itemById = itemRepository.findById(userId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        itemValidator.validateNonNullFields(itemDto);

        Item item = itemMapper.mapToItem(itemDto, itemById, userId, itemId);
        Item updatedItem = itemRepository.update(item);

        return itemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(userId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto findById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        return itemRepository.findAllByUserId(userId);
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        List<Item> result = itemRepository.search(text);

        return result.stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
