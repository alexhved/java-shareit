package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMemServiceImpl implements UserService {
    private final UserMemRepository userMemRepository;
    private final UserValidatorImpl userValidator;
    private final UserMapper userMapper;

    @Override
    public UserDto save(UserDto userDto) {
        userValidator.validateAllFields(userDto);

        User newUser = userMapper.mapToUser(userDto);
        User savedUser = userMemRepository.save(newUser);

        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto findById(long id) {
        User user = userMemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("user with id: %s not found", id)));
        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateById(long userId, UserDto userDto) {
        User userById = userMemRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("user with id: %s not found", userId)));

        userValidator.validateNonNullFields(userDto);

        User user = userMapper.mapToUser(userDto, userById);
        User updatedUser = userMemRepository.update(userId, user);

        return userMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteById(long userId) {
        userMemRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userMemRepository.findAll();
        return users.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
