package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface Mapper {

    User mapToUser(UserDto userDto);
    User mapToUser(UserDto userDto, User user);
    UserDto mapToUserDto(User user);
}
