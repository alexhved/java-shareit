package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody RequestDto requestDto) {

        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findOneByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Positive @PathVariable Long requestId) {

        return requestClient.findOneByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                          @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {

        return requestClient.findAll(userId, from, size);
    }
}
