package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    @Test
    void getNextBookings() {
        User user = new User(0L, "otherUser", "otheremail@.com");
        Item item =
                new Item(0L, user, "refrigerator", "simple refro machine", true, null);
        Booking booking =
                new Booking(0L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Status.APPROVED);

        em.persist(user);
        Long id = em.persistAndGetId(item, Long.class);
        em.persist(booking);

        List<Booking> nextBookings = repository.getNextBookings(List.of(id));

        assertEquals(nextBookings.get(0).getStart(), booking.getStart());
    }
}