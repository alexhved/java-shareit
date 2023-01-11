package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto user);

    UserDto findById(long id);

    UserDto updateById(long userId, UserDto user);

    void deleteById(long userId);

    List<UserDto> findAll();

}
