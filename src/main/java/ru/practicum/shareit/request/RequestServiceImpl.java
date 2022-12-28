package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Autowired
    public RequestServiceImpl(UserRepository userRepository, RequestRepository requestRepository,
                              RequestMapper requestMapper) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
    }

    @Override
    public ResponseDto create(Long userId, RequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceAccessException("Wrong user id"));

        ItemRequest newItemRequest = requestMapper.mapToItemRequest(requestDto);
        newItemRequest.setRequester(user);
        newItemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = requestRepository.save(newItemRequest);
        return requestMapper.mapToResponseDto(savedItemRequest);
    }

    @Override
    public List<ResponseDto> findAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceAccessException("Wrong user id");
        }

        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        return itemRequests.stream()
                .map(requestMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseDto findOneByRequestId(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceAccessException("Wrong user id");
        }

        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        return requestMapper.mapToResponseDto(itemRequest);
    }

    @Override
    public List<ResponseDto> findAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceAccessException("Wrong user id");
        }

        List<ItemRequest> requestList;

        if (from == null || size == null) {
            requestList = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        } else {
            if (from < 0 || size < 1) {
                throw new IllegalArgumentException("Illegal argument");
            }

            Sort sort = Sort.by(Sort.Direction.DESC, "created");
            Pageable page = PageRequest.of((from / size), size, sort);
            Page<ItemRequest> itemRequestPage = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, page);
            requestList = itemRequestPage.getContent();
        }

        return requestList.stream()
                .map(requestMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }
}
