package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UserTest {

    long id = 1L;
    String name = "user";
    String email = "user@email.com";
    private final User user = new User(id, name, email);

    @Test
    void getId() {
        long userId = user.getId();
        assertEquals(userId, id);
    }

    @Test
    void getName() {
        String userName = user.getName();
        assertEquals(userName, name);
    }

    @Test
    void getEmail() {
        String userEmail = user.getEmail();
        assertEquals(user.getEmail(), email);
    }

    @Test
    void getItems() {
        List<Item> items = user.getItems();
        assertNull(items);
    }

    @Test
    void setId() {
        user.setId(2L);
        assertEquals(user.getId(), 2L);
    }

    @Test
    void setName() {
        user.setName("baran");
        assertEquals(user.getName(), "baran");
    }

    @Test
    void setEmail() {
        user.setEmail("email");
        assertEquals(user.getEmail(), "email");
    }

    @Test
    void setItems() {
        user.setItems(List.of(new Item()));
        assertFalse(user.getItems().isEmpty());
    }

    @Test
    void equals() {
        User otherUser = new User(1L, "name", "email");
        assertEquals(user, otherUser);
    }
}