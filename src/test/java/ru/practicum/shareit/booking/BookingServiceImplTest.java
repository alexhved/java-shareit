package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.BookingStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingValidator validator;
    private final BookingMapper bookingMapper = new BookingMapper(new ItemMapper());
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User user2;
    private Item item;
    private Long userId;
    private Long userId2;
    private Long itemId;
    private Long bookingId;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", bookingMapper);
        userId = 1L;
        itemId = 1L;
        userId2 = 2L;
        user = new User(userId, "user", "user@email.com");
        user2 = new User(userId2, "user2", "user2@email.com");
        item = new Item(1L, user2, "potato", "description", true, null);
        bookingId = 1L;
        bookingRequestDto =
                new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, Status.WAITING);

        booking = bookingMapper.mapToBooking(bookingRequestDto, user, item);
        bookingResponseDto = bookingMapper.mapToResponseDto(booking, user, item);

        item.setOwner(user2);
    }

    @Test
    void create_shouldReturnDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto dto = bookingService.create(userId, bookingRequestDto);

        assertEquals(bookingResponseDto, dto);
        verify(validator, times(1)).validateNonNullFields(bookingRequestDto);
    }


    @Test
    void create_shouldThrowResourceAccessEx() {
        item.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ResourceAccessException.class, () -> bookingService.create(userId, bookingRequestDto));
    }

    @Test
    void create_shouldThrowIllegalArgumentEx() {
        item.setAvailable(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(userId, bookingRequestDto));
    }

    @Test
    void approve_shouldApprove() {
        item.setOwner(user);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto approved = bookingService.approve(userId, bookingId, true);

        assertEquals(Status.APPROVED, approved.getStatus());
    }

    @Test
    void approve_shouldReject() {
        item.setOwner(user);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto approved = bookingService.approve(userId, bookingId, false);

        assertEquals(Status.REJECTED, approved.getStatus());
    }

    @Test
    void approve_shouldThrowResourceAccessEx() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        assertThrows(ResourceAccessException.class, () -> bookingService.approve(userId, bookingId, true));
    }

    @Test
    void approve_shouldThrowBookingStatusEx() {
        item.setOwner(user);
        booking.setStatus(Status.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        assertThrows(BookingStatusException.class, () -> bookingService.approve(userId, bookingId, true));
    }

    @Test
    void getByBookerOrOwner_shouldReturnDto() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        BookingResponseDto dto = bookingService.getByBookerOrOwner(userId, bookingId);

        assertNotNull(dto);
    }

    @Test
    void getByBookerOrOwner_shouldEquals() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        BookingResponseDto dto = bookingService.getByBookerOrOwner(userId2, bookingId);

        assertEquals(userId, dto.getBooker().getId());
    }

    @Test
    void getByBookerOrOwner_shouldThrowResourceAccessEx() {
        item.setOwner(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(ResourceAccessException.class, () -> bookingService.getByBookerOrOwner(userId2, bookingId));
    }

    @Test
    void findAllByBookerIdAndState_shouldReturnEmptyList() {
        String state = "ALL";
        int from = 1;
        int size = 1;
        user.setItems(List.of(item));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(userId, state, from, size);

        assertTrue(dtoList.isEmpty());
    }

    @Test
    void findAllByOwnerIdAndState_shouldReturnEmptyList() {
        String state = "ALL";
        int from = 1;
        int size = 1;
        user.setItems(List.of(item));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        List<BookingResponseDto> dtoList = bookingService.findAllByBookerIdAndState(userId, state, from, size);

        assertTrue(dtoList.isEmpty());
    }
}