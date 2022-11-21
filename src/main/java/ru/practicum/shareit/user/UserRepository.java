package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    User save(User user);
    User update(long userId, User user);
    Optional<User> findById(long userId);
    void deleteById(long userId);
}
