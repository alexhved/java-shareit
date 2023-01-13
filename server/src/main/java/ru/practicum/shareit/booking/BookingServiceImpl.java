package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.BookingStatusException;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));

        long itemId = bookingRequestDto.getItemId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        if (item.getOwner().getId() == userId) {
            throw new ResourceAccessException("wrong user id");
        }

        if (!item.getAvailable()) {
            throw new IllegalArgumentException(String.format("Item with id %s is not available now", itemId));
        }

        bookingRequestDto.setStatus(Status.WAITING);
        Booking booking = bookingMapper.mapToBooking(bookingRequestDto, user, item);

        Booking saved = bookingRepository.save(booking);

        return bookingMapper.mapToResponseDto(saved, user, item);
    }

    @Override
    public BookingResponseDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Booking with id %s not found", bookingId)));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new ResourceAccessException("No access");
        }

        if (Objects.requireNonNull(booking.getStatus()) == Status.WAITING) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new BookingStatusException("Illegal booking status");
        }

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.mapToResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto getByBookerOrOwner(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Booking with id %s not found", bookingId)));

        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return bookingMapper.mapToResponseDto(booking);
        } else {
            throw new ResourceAccessException("No access");
        }
    }

    @Override
    public List<BookingResponseDto> findAllByBookerIdAndState(Long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %s not found", userId));
        }

        Pageable pageable = PageRequest.of((from / size), size);

        Page<Booking> bookingPage = getPageableBookingsByBookerAndState(userId, state, pageable);
        List<Booking> bookings = bookingPage.getContent();

        return bookings.stream()
                .map(bookingMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findAllByOwnerIdAndState(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));

        if (user.getItems() == null || user.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemsIds = user.getItems().stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of((from / size), size);

        Page<Booking> bookingsByState = getBookingsByOwnerAndState(state, itemsIds, pageable);

        return bookingsByState.stream()
                .map(bookingMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private Page<Booking> getPageableBookingsByBookerAndState(Long userId, String state, Pageable page) {
        State enumState = State.valueOf(state);
        Page<Booking> bookingsByState;

        switch (enumState) {
            case ALL:
                bookingsByState = bookingRepository.findByBookerIdOrderByStartDesc(userId, page);
                break;
            case PAST:
                bookingsByState = bookingRepository.findByBookerAndStatePast(userId, page);
                break;
            case CURRENT:
                bookingsByState = bookingRepository.findByBookerIdAndCurrent(userId, page);
                break;
            case FUTURE:
                bookingsByState = bookingRepository.findByBookerAndStateFuture(userId, page);
                break;
            case WAITING:
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case REJECTED:
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                break;
            default:
                bookingsByState = null;
                break;
        }
        return bookingsByState;
    }

    private Page<Booking> getBookingsByOwnerAndState(String state, List<Long> itemsIds, Pageable page) {
        State enumState = State.valueOf(state);
        Page<Booking> bookingsByState;

        switch (enumState) {
            case ALL:
                bookingsByState = bookingRepository.findByItemsAndStateAll(itemsIds, page);
                break;
            case PAST:
                bookingsByState = bookingRepository.findByItemsAndStatePast(itemsIds, page);
                break;
            case CURRENT:
                bookingsByState = bookingRepository.findByItemsAndStateCurrent(itemsIds, page);
                break;
            case FUTURE:
                bookingsByState = bookingRepository.findByItemsAndStateFuture(itemsIds, page);
                break;
            case WAITING:
                bookingsByState = bookingRepository.findByItemsAndState(itemsIds, Status.WAITING, page);
                break;
            case REJECTED:
                bookingsByState = bookingRepository.findByItemsAndState(itemsIds, Status.REJECTED, page);
                break;
            default:
                bookingsByState = null;
                break;
        }
        return bookingsByState;
    }
}
