package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {
     Item save(Item newItem);

    Optional<Item> findById(long userId, long itemId);

    Optional<Item> findById(long itemId);

    Item update(Item item);

    List<Item> findAllByUserId(long userId);

    void delete(long userId, long itemId);

    List<Item> findAll();

    List<Item> search(String text);
}
