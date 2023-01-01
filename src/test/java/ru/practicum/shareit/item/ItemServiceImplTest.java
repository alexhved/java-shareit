package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemValidator itemValidator;
    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    ItemServiceImpl itemService;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1, "refrigerator",
            "simple refro machine", true, null);
    private final ShortBooking lastBooking = new ShortBooking(1, 1);
    private final ShortBooking nextBooking = new ShortBooking(1, 1);
    private final ItemResponseDto itemResponseDto = new ItemResponseDto(1, "refrigerator",
            "simple refro machine", true, 0, lastBooking, nextBooking, new ArrayList<>());
    private final User user = new User(1, "user", "email@.com");
    private final ItemRequest request = new ItemRequest(1, "description", user, LocalDateTime.now(), new ArrayList<>());
    private final Item item = new Item(1, user, "refrigerator",
            "simple refro machine", true, null);
    private final Comment comment = new Comment(1, "comment txt", item, user, LocalDateTime.now());

    private final CommentRequestDto commentRequestDto = new CommentRequestDto("comment txt");

    private final Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, Status.APPROVED);
    private final Booking booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), item, user, Status.APPROVED);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
    }

    @Test
    void save_thenReturnDtoWithoutRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        ItemResponseDto saved = itemService.save(1, itemRequestDto);

        verify(itemValidator, Mockito.times(1)).validateAllFields(itemRequestDto);
        verify(requestRepository, never()).save(any());
        assertThat(saved)
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("requestId", 0L);
    }

    @Test
    void save_thenReturnDtoWithRequest() {
        itemRequestDto.setRequestId(1L);
        item.setRequest(request);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        ItemResponseDto saved = itemService.save(1, itemRequestDto);

        verify(itemValidator, Mockito.times(1)).validateAllFields(itemRequestDto);
        assertThat(saved)
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("requestId", 1L);
    }

    @Test
    void save_thenThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.save(1L, itemRequestDto));
        verify(itemRepository, never()).save(any());
    }


    @Test
    void update_shouldReturnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemResponseDto updated = itemService.update(1L, 1L, itemRequestDto);

        verify(itemValidator, Mockito.times(1)).validateNonNullFields(itemRequestDto);
        assertThat(updated)
                .hasFieldOrPropertyWithValue("id", itemRequestDto.getId())
                .hasFieldOrPropertyWithValue("name", itemRequestDto.getName());
    }

    @Test
    void update_shouldThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> itemService.update(1L, 1L, itemRequestDto));
        verify(itemValidator, never()).validateNonNullFields(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void findById_shouldThrown() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> itemService.findById(1L, 1L));
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findByItemId(any());
    }

    @Test
    void findById_shouldReturnItemResponseDto() {
        List<Comment> comments = List.of(comment);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(comments);
        when(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking1));
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking2));

        ItemResponseDto itemResponseDto1 = itemService.findById(1L, 1L);

        assertThat(itemResponseDto1)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", item.getName());

        assertThat(itemResponseDto1.getLastBooking())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookerId", 1L);

        assertThat(itemResponseDto1.getNextBooking())
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("bookerId", 1L);
    }

    @Test
    void findAllByUserId_shouldThrownNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> itemService.findAllByUserId(1L, 2, 2));
        verify(itemRepository, never()).findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class));
        verify(commentRepository, never()).findByItemId(any());
        verify(bookingRepository, never()).getLastBookings(any());
        verify(bookingRepository, never()).getNextBookings(any());
    }

    @Test
    void findAllByUserId_shouldThrownIllegalArgument() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemService.findAllByUserId(1L, 0, 0));
        verify(itemRepository, never()).findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class));
        verify(commentRepository, never()).findByItemId(any());
        verify(bookingRepository, never()).getLastBookings(any());
        verify(bookingRepository, never()).getNextBookings(any());
    }

    @Test
    void searchItem_shouldThrownNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.searchItem(1L, "text", null, null));
        verify(itemRepository, never())
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any());
    }

    @Test
    void searchItem_shouldThrowIllegalArgument() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> itemService.searchItem(1L, "text", 0, 0));
        verify(itemRepository, never())
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any());
    }

    @Test
    void searchItem_shouldReturnResultList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        List<ItemResponseDto> resultListText = itemService.searchItem(1, "text", 1, 2);
        assertTrue(resultListText.isEmpty());

        List<ItemResponseDto> resultListTextUnpaged = itemService.searchItem(1, "text", null, null);
        assertTrue(resultListTextUnpaged.isEmpty());

        List<ItemResponseDto> resultListTXT = itemService.searchItem(1, "txt", 1, 2);
        assertTrue(resultListTXT.isEmpty());

        List<ItemResponseDto> resultListTX = itemService.searchItem(1, "tx", 1, 2);
        assertTrue(resultListTX.isEmpty());
    }

    @Test
    void searchItem_shouldReturnResultEmptyList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<ItemResponseDto> resultList = itemService.searchItem(1, "", 1, 2);

        assertTrue(resultList.isEmpty());
    }

    @Test
    void addComment_shouldThrowNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> itemService.addComment(1L, 1L, commentRequestDto));

        verify(commentValidator, Mockito.times(1)).validateAllFields(commentRequestDto);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_shouldThrowIllegalArgument() {
        List<Booking> bookings = Collections.emptyList();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(bookings);

        assertThrows(IllegalArgumentException.class, () -> itemService.addComment(1L, 1L, commentRequestDto));

        verify(commentValidator, Mockito.times(1)).validateAllFields(commentRequestDto);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_shouldReturnComment() {
        List<Booking> bookings = List.of(new Booking());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(bookings);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto commentResponseDto = itemService.addComment(1L, 1L, commentRequestDto);

        assertThat(commentResponseDto)
                .hasFieldOrPropertyWithValue("text", commentRequestDto.getText());
    }
}