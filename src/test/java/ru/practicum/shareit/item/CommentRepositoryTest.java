package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository repository;

    @Test
    void findByItemId() {
        User user = new User(0L, "name", "email");
        Item item = new Item(0L, user, "name", "description", true, null);
        Comment comment = new Comment(0L, "text", item, user, LocalDateTime.now());

        em.persist(user);
        em.persist(item);
        em.persist(comment);

        List<Comment> comments = repository.findByItemId(List.of(item.getId()));

        assertFalse(comments.isEmpty());
        assertThat(comments.get(0))
                .hasFieldOrPropertyWithValue("text", "text")
                .hasFieldOrPropertyWithValue("created", comment.getCreated());
    }
}