package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.util.List;


public interface RequestService {
    ResponseDto create(Long userId, RequestDto requestDto);

    List<ResponseDto> findAllByUserId(Long userId);

    ResponseDto findOneByRequestId(Long userId, Long requestId);

    List<ResponseDto> findAll(Long userId, Integer from, Integer size);
}
