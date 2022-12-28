package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    long id = 1L;
    String description = "description";

    User user = new User();
    LocalDateTime now = LocalDateTime.now();
    List<Item> items = List.of(new Item());
    private final ItemRequest itemRequest = new ItemRequest(id, description, user, now, items);
    private final ItemRequest otherItemRequest = new ItemRequest(id, description, user, now, items);

    @Test
    void testEquals() {
        boolean equals = itemRequest.equals(otherItemRequest);
        assertTrue(equals);
    }

    @Test
    void testHashCode() {
        int hashCode = itemRequest.hashCode();
        assertTrue(hashCode != 0);
    }

    @Test
    void getId() {
        long requestId = itemRequest.getId();
        assertEquals(id, requestId);
    }

    @Test
    void getDescription() {
        String requestDescription = itemRequest.getDescription();
        assertEquals(description, requestDescription);
    }

    @Test
    void getRequester() {
        User requester = itemRequest.getRequester();
        assertEquals(user, requester);
    }

    @Test
    void getCreated() {
        LocalDateTime created = itemRequest.getCreated();
        assertEquals(now, created);
    }

    @Test
    void getItems() {
        List<Item> requestItems = itemRequest.getItems();
        assertEquals(items, requestItems);
    }

    @Test
    void setId() {
        itemRequest.setId(2L);
        assertNotEquals(id, itemRequest.getId());
    }

    @Test
    void setDescription() {
        itemRequest.setDescription("other description");
        assertNotEquals(description, itemRequest.getDescription());
    }

    @Test
    void setRequester() {
        itemRequest.setRequester(new User(2L, "name", "email"));
        assertNotEquals(user, itemRequest.getRequester());
    }

    @Test
    void setCreated() {
        itemRequest.setCreated(LocalDateTime.now());
        assertNotEquals(now, itemRequest.getCreated());
    }

    @Test
    void setItems() {
        itemRequest.setItems(List.of());
        assertNotEquals(items, itemRequest.getItems());
    }
}