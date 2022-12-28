package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @InjectMocks
    RequestServiceImpl requestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    private final RequestMapper requestMapper = new RequestMapperImpl();

    User user;
    long userId;
    RequestDto requestDto;
    ResponseDto responseDto;

    ItemRequest itemRequest;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(requestService, "requestMapper", requestMapper);
        user = new User(1L, "name", "name@mail.com");
        userId = 1L;
        requestDto = new RequestDto("description");
        itemRequest = requestMapper.mapToItemRequest(requestDto);
        itemRequest.setId(1L);
        responseDto = requestMapper.mapToResponseDto(itemRequest);
    }

    @Test
    void create_shouldReturnResponseDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ResponseDto responseDto1 = requestService.create(userId, requestDto);

        assertEquals(responseDto1, responseDto);
    }

    @Test
    void findAllByUserId_shouldReturnDtoList() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));

        List<ResponseDto> dtoList = requestService.findAllByUserId(userId);

        assertEquals(List.of(responseDto), dtoList);
    }

    @Test
    void findAllByUserId_shouldThrowResourceAccess() {
        when(userRepository.existsById(userId)).thenThrow(ResourceAccessException.class);

        assertThrows(ResourceAccessException.class, () -> requestService.findAllByUserId(userId));

    }

    @Test
    void findOneByRequestId_shouldReturnDto() {
        long requestId = itemRequest.getId();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.ofNullable(itemRequest));

        ResponseDto responseDto1 = requestService.findOneByRequestId(userId, requestId);

        assertEquals(responseDto, responseDto1);
    }

    @Test
    void findOneByRequestId_shouldThrow() {
        long requestId = itemRequest.getId();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceAccessException.class, () -> requestService.findOneByRequestId(userId, requestId));
    }

    @Test
    void findAll_shouldReturnDtoList() {
        int from = 1;
        int size = 1;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());

        List<ResponseDto> dtoList = requestService.findAll(userId, from, size);

        assertTrue(dtoList.isEmpty());
    }

    @Test
    void findAllWithNulls_shouldReturnDtoList() {
        Integer from = null;
        Integer size = null;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(new ArrayList<>());

        List<ResponseDto> dtoList = requestService.findAll(userId, from, size);

        assertTrue(dtoList.isEmpty());
    }

    @Test
    void findAll_shouldThrowAccessException() {
        int from = 1;
        int size = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceAccessException.class, () -> requestService.findAll(userId, from, size));
    }

    @Test
    void findAll_shouldThrowIllegalArgument() {
        int from = 0;
        int size = 0;

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> requestService.findAll(userId, from, size));
    }
}