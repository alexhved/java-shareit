package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Map<Long, User> userMap = new HashMap<>();
    private static long id = 1;

    private static long nextId() {
        return id++;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User save(User user) {
        user.setId(nextId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long userId, User user) {
        user.setId(userId);
        userMap.put(userId, user);
        return user;
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public void deleteById(long userId) {
        userMap.remove(userId);
    }
}
