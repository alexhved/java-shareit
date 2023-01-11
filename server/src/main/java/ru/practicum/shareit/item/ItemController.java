package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto save(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.save(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long itemId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.update(userId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        return itemService.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(required = false, defaultValue = "") String text,
                                            @RequestParam(required = false) Integer from,
                                            @RequestParam(required = false) Integer size) {

        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @RequestBody CommentRequestDto commentRequestDto) {

        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}
