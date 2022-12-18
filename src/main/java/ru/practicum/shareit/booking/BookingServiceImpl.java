package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingValidator validator;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                              BookingRepository bookingRepository, BookingValidator validator,
                              BookingMapper bookingMapper) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.validator = validator;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) {
        validator.validateNonNullFields(bookingRequestDto);

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
    public List<BookingResponseDto> findAllByBookerIdAndState(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %s not found", userId));
        }

        List<Booking> bookingsByState = getBookingsByBookerAndState(userId, state);

        return bookingsByState.stream()
                .map(bookingMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findAllByOwnerIdAndState(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));

        if (user.getItems() == null || user.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemsIds = user.getItems().stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookingsByState = getBookingsByOwnerAndState(state, itemsIds);

        return bookingsByState.stream()
                .map(bookingMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByOwnerAndState(String state, List<Long> itemsIds) {

        State enumState = Arrays.stream(State.values())
                .filter(value -> value.name().equals(state))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", state)));

        List<Booking> bookingsByState;

        switch (enumState) {
            case ALL:
                bookingsByState = bookingRepository.findByItemsAndStateAll(itemsIds);
                break;
            case PAST:
                bookingsByState = bookingRepository.findByItemsAndStatePast(itemsIds);
                break;
            case CURRENT:
                bookingsByState = bookingRepository.findByItemsAndStateCurrent(itemsIds);
                break;
            case FUTURE:
                bookingsByState = bookingRepository.findByItemsAndStateFuture(itemsIds);
                break;
            case WAITING:
                bookingsByState = bookingRepository.findByItemsAndState(itemsIds, Status.WAITING);
                break;
            case REJECTED:
                bookingsByState = bookingRepository.findByItemsAndState(itemsIds, Status.REJECTED);
                break;
            default:
                bookingsByState = Collections.emptyList();
                break;
        }
        return bookingsByState;
    }

    private List<Booking> getBookingsByBookerAndState(Long userId, String state) {

        State enumState = Arrays.stream(State.values())
                .filter(value -> value.name().equals(state))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", state)));

        List<Booking> bookingsByState;

        switch (enumState) {
            case ALL:
                bookingsByState = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookingsByState = bookingRepository.findByBookerAndStatePast(userId);
                break;
            case CURRENT:
                bookingsByState = bookingRepository.findByBookerIdAndCurrent(userId);
                break;
            case FUTURE:
                bookingsByState = bookingRepository.findByBookerAndStateFuture(userId);
                break;
            case WAITING:
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                bookingsByState = Collections.emptyList();
                break;
        }
        return bookingsByState;
    }
}
