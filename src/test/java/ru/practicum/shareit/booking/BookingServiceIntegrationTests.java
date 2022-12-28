package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTests {
    private final EntityManager entityManager;

    private final BookingService bookingService;

    private final User user = new User(0L, "user", "email@.com");
    private final User otherUser = new User(0L, "otherUser", "otheremail@.com");
    private final Item item =
            new Item(0L, user, "refrigerator", "simple refro machine", true, null);

    private final Item otherItem =
            new Item(0L, otherUser, "superrefrigerator", "super refro machine", true, null);
    private final Booking booking =
            new Booking(0L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, Status.APPROVED);


    @Test
    void findAllByBookerIdAndState_shouldReturnALL() {
        otherUser.setItems(List.of(otherItem));
        booking.setBooker(otherUser);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(otherUser.getId(), "ALL", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_shouldReturnPAST() {
        otherUser.setItems(List.of(otherItem));
        booking.setBooker(otherUser);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(otherUser.getId(), "PAST", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_shouldReturnCURRENT() {
        booking.setEnd(LocalDateTime.now().plusDays(1));
        otherUser.setItems(List.of(otherItem));
        booking.setBooker(otherUser);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(otherUser.getId(), "CURRENT", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_shouldReturnFUTURE() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setStart(LocalDateTime.now().plusDays(2));
        otherUser.setItems(List.of(otherItem));
        booking.setBooker(otherUser);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(otherUser.getId(), "FUTURE", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_shouldReturnWAITING() {
        booking.setStatus(Status.WAITING);
        otherUser.setItems(List.of(otherItem));
        booking.setBooker(otherUser);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(otherUser.getId(), "WAITING", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_shouldReturnREJECTED() {
        booking.setStatus(Status.REJECTED);
        otherUser.setItems(List.of(otherItem));
        booking.setBooker(otherUser);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(otherUser.getId(), "REJECTED", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnALL() {
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByOwnerIdAndState(user.getId(), "ALL", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnPAST() {
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByOwnerIdAndState(user.getId(), "PAST", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnCURRENT() {
        booking.setEnd(LocalDateTime.now().plusDays(1));
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByOwnerIdAndState(user.getId(), "CURRENT", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnFUTURE() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setStart(LocalDateTime.now().plusDays(2));
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByOwnerIdAndState(user.getId(), "FUTURE", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnWAITING() {
        booking.setStatus(Status.WAITING);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByOwnerIdAndState(user.getId(), "WAITING", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnREJECTED() {
        booking.setStatus(Status.REJECTED);
        user.setItems(List.of(item));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<BookingResponseDto> dtoList = bookingService.findAllByOwnerIdAndState(user.getId(), "REJECTED", 0, 1);

        Assertions.assertThat(dtoList).isNotEmpty();
        Assertions.assertThat(dtoList.get(0))
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", booking.getStatus());
    }
}
