package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final ItemValidator itemValidator;
    private final CommentValidator commentValidator;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemServiceImpl(UserRepository userRepository,
                           ItemRepository itemRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           RequestRepository requestRepository,
                           ItemValidator itemValidator,
                           CommentValidator commentValidator,
                           ItemMapper itemMapper,
                           CommentMapper commentMapper) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
        this.itemValidator = itemValidator;
        this.commentValidator = commentValidator;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public ItemResponseDto save(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        itemValidator.validateAllFields(itemRequestDto);

        Long requestId = itemRequestDto.getRequestId();
        Optional<ItemRequest> itemRequest = Optional.empty();

        if (requestId != null) {
            itemRequest = requestRepository.findById(requestId);
        }

        Item newItem = itemMapper.mapToItem(itemRequestDto, user, itemRequest);
        Item savedItem = itemRepository.save(newItem);

        return itemMapper.mapToItemResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto update(long userId, long itemId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        Item itemById = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        itemValidator.validateNonNullFields(itemRequestDto);


        Item item = itemMapper.mapToItem(itemRequestDto, itemById, user);
        Item updatedItem = itemRepository.save(item);

        return itemMapper.mapToItemResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto findById(long userId, long itemId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %s not found", userId));
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id %s not found", itemId)));

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentResponseDto> commentResponseDtoList = comments.stream()
                .map(commentMapper::mapToCommentResponseDto)
                .collect(Collectors.toList());

        ItemResponseDto itemResponseDto = itemMapper.mapToItemResponseDto(item, commentResponseDtoList);

        if (item.getOwner().getId() == userId) {
            Optional<Booking> optLastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now());
            Optional<Booking> optNextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now());
            if (optLastBooking.isPresent()) {
                Booking lastBooking = optLastBooking.get();
                itemResponseDto.setLastBooking(new ShortBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
            if (optNextBooking.isPresent()) {
                Booking nextBooking = optNextBooking.get();
                itemResponseDto.setNextBooking(new ShortBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
        }
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> findAllByUserId(long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %s not found", userId));
        }

        Pageable pageable;
        if (from == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            if (from < 0 || size < 1) {
                throw new IllegalArgumentException("Illegal pageable argument");
            }
            pageable = PageRequest.of(from, size);
        }

        Page<Item> itemsPage = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageable);
        List<Item> items = itemsPage.getContent();

        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findByItemId(itemsIds);
        List<Booking> lastBookings = bookingRepository.getLastBookings(itemsIds);
        List<Booking> nextBookings = bookingRepository.getNextBookings(itemsIds);

        List<ItemResponseDto> responseDtoList = new ArrayList<>();

        for (Item item : items) {
            ItemResponseDto itemResponseDto = itemMapper.mapToItemResponseDto(item);
            List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

            if (!comments.isEmpty()) {
                for (Comment comment : comments) {
                    if (comment.getItem().getId() == item.getId()) {
                        commentResponseDtoList.add(commentMapper.mapToCommentResponseDto(comment));
                    }
                }
            }

            if (!lastBookings.isEmpty()) {
                for (Booking last : lastBookings) {
                    if (last.getItem().getId() == item.getId()) {
                        itemResponseDto.setLastBooking(new ShortBooking(last.getId(), last.getBooker().getId()));
                    }
                }
            }

            if (!nextBookings.isEmpty()) {
                for (Booking next : nextBookings) {
                    if (next.getItem().getId() == item.getId()) {
                        itemResponseDto.setNextBooking(new ShortBooking(next.getId(), next.getBooker().getId()));
                    }
                }
            }
            itemResponseDto.setComments(commentResponseDtoList);
            responseDtoList.add(itemResponseDto);
        }

        return responseDtoList;
    }

    @Override
    public List<ItemResponseDto> searchItem(long userId, String text, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        CharSequence textSeq;

        if (text.length() > 4) {
            textSeq = text.toLowerCase().subSequence(0, text.length() - 2);
        } else if (text.length() > 3) {
            textSeq = text.toLowerCase().subSequence(0, text.length() - 1);
        } else {
            textSeq = text.toLowerCase().subSequence(0, text.length());
        }
        String searchValue = String.valueOf(textSeq);

        Pageable pageable;
        if (from == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            if (from < 0 || size < 1) {
                throw new IllegalArgumentException("Illegal pageable argument");
            }
            pageable = PageRequest.of(from, size);
        }

        Page<Item> page = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(searchValue, searchValue, pageable);
        List<Item> result = page.getContent();

        return result.stream()
                .map(itemMapper::mapToItemResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentResponseDto addComment(long userId, long itemId, CommentRequestDto commentRequestDto) {
        commentValidator.validateAllFields(commentRequestDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User id"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Item id"));

        List<Booking> bookings = bookingRepository
                .findByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("Booking with booker id: %s and item id: %s", userId, itemId));
        }

        Comment newComment = commentMapper.mapToComment(commentRequestDto, user, item);
        Comment savedComment = commentRepository.save(newComment);

        return commentMapper.mapToCommentResponseDto(savedComment);
    }
}
