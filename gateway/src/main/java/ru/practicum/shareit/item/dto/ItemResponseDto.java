package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;
    private ShortBooking lastBooking;
    private ShortBooking nextBooking;
    private List<CommentResponseDto> comments;
}
