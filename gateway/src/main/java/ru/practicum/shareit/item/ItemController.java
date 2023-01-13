package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final ItemValidator itemValidator;

    @PostMapping
    public ResponseEntity<Object> save(@Min(1) @RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemClient.save(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Min(1) @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Min(1) @PathVariable long itemId,
                                         @RequestBody ItemRequestDto itemRequestDto) {

        itemValidator.validateNonNullFields(itemRequestDto);
        return itemClient.update(userId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@Min(1) @RequestHeader("X-Sharer-User-Id") long userId,
                                           @Positive @PathVariable long itemId) {
        return itemClient.findById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(required = false, defaultValue = "") String text,
                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {

        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentRequestDto commentRequestDto) {

        return itemClient.addComment(userId, itemId, commentRequestDto);
    }

}
