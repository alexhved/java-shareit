package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Not valid email")
    private String email;
    private final Map<Long, Item> items = new HashMap<>();
}
