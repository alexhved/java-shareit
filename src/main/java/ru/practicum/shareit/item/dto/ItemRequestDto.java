package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    @PositiveOrZero(message = "Id must be positive or zero")
    private long id;
    @NotBlank(message = "name cannot be empty")
    private String name;
    @NotBlank(message = "description cannot be empty")
    private String description;
    @NotNull(message = "Available must be true or false")
    private Boolean available;
    @Nullable
    private Long requestId;
}
