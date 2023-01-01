package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void getId() {
        long id = 1L;
        Comment comment = new Comment(1L, null, null, null, null);
        long commentId = comment.getId();
        assertEquals(id, commentId);
    }

    @Test
    void equals() {
        Comment comment = new Comment(1L, null, null, null, null);
        Comment comment2 = new Comment(1L, null, null, null, null);
        boolean equals = comment.equals(comment2);
        assertTrue(equals);

    }
}