package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final EntityManager entityManager;

    private final ItemService itemService;

    private final User user = new User(0L, "userrr", "userrr@email.com");
    private final Item item =
            new Item(0L, user, "refrigerator", "simple refro machine", true, null);
    private final Comment comment = new Comment(0L, "comment txt", item, user, LocalDateTime.now());
    private final Booking booking =
            new Booking(0L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, Status.APPROVED);

    private final Booking otherBooking =
            new Booking(0L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Status.APPROVED);

    @Test
    void findAllByUserId_ReturnDtoList() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);
        entityManager.persist(otherBooking);
        entityManager.persist(comment);

        List<ItemResponseDto> dtoList = itemService.findAllByUserId(user.getId(), 0, 1);

        assertFalse(dtoList.isEmpty());

        assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", true);

        assertThat(dtoList.get(0).getLastBooking())
                .hasFieldOrPropertyWithValue("bookerId", user.getId());

        assertThat(dtoList.get(0).getNextBooking())
                .hasFieldOrPropertyWithValue("bookerId", user.getId());

        assertThat(dtoList.get(0).getComments().get(0))
                .hasFieldOrPropertyWithValue("text", comment.getText());

    }

    @Test
    void findAllByUserIdWithUnpaged_ReturnDtoList() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);
        entityManager.persist(comment);

        List<ItemResponseDto> dtoList = itemService.findAllByUserId(user.getId(), null, null);

        assertFalse(dtoList.isEmpty());

        assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", true);

        assertThat(dtoList.get(0).getLastBooking())
                .hasFieldOrPropertyWithValue("bookerId", user.getId());


        assertThat(dtoList.get(0).getComments().get(0))
                .hasFieldOrPropertyWithValue("text", comment.getText());

    }

}
