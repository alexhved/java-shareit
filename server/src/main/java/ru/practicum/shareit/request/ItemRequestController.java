package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody RequestDto requestDto) {

        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ResponseDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseDto findOneByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long requestId) {

        return requestService.findOneByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public List<ResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size) {

        return requestService.findAll(userId, from, size);
    }
}
