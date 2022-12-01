package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, List<Item>> itemMap = new HashMap<>();
    private static long id = 1;

    private static long genId() {
        return id++;
    }


    @Override
    public Item save(Item newItem) {
        newItem.setId(genId());

        List<Item> userItems;
        if (!itemMap.containsKey(newItem.getUserId())) {
            userItems = new ArrayList<>();
        } else {
            userItems = itemMap.get(newItem.getUserId());
        }
        userItems.add(newItem);
        itemMap.put(newItem.getUserId(), userItems);

        return newItem;
    }

    @Override
    public Optional<Item> findById(long userId, long itemId) {
        List<Item> userItems = itemMap.get(userId);
        if (userItems != null) {
            return itemMap.get(userId).stream()
                    .filter(item -> item.getId() == itemId)
                    .findFirst();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return itemMap.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public Item update(Item item) {
        Item oldItem = findById(item.getUserId(), item.getId()).orElseThrow();
        List<Item> userItems = itemMap.get(item.getUserId());

        userItems.remove(oldItem);
        userItems.add(item);

        itemMap.put(item.getUserId(), userItems);

        return item;
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        List<Item> userItems = itemMap.get(userId);
        if (userItems == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(itemMap.get(userId));

    }

    @Override
    public void delete(long userId, long itemId) {
        List<Item> userItems = itemMap.get(userId);

        userItems.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst().ifPresent(userItems::remove);
    }

    @Override
    public List<Item> findAll() {
        return itemMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> result = new ArrayList<>();
        CharSequence textSeq;

        if (text.length() > 4) {
            textSeq = text.toLowerCase().subSequence(0, text.length() - 2);
        } else if (text.length() > 3) {
            textSeq = text.toLowerCase().subSequence(0, text.length() - 1);
        } else {
            textSeq = text.toLowerCase().subSequence(0, text.length());
        }

        itemMap.values().stream()
                .flatMap(Collection::stream)
                .forEach(item -> {
                    if (item.getName().toLowerCase().contains(textSeq) && item.getAvailable().equals(true)
                            || item.getDescription().toLowerCase().contains(textSeq) && item.getAvailable().equals(true)) {
                        result.add(item);
                    }
                });

        return result;
    }
}
