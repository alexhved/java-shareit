package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class UserJpaServiceImpl implements  UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserValidatorImpl userValidator;
    private final UserMapper userMapper;

    @Autowired
    public UserJpaServiceImpl(UserJpaRepository userJpaRepository, UserValidatorImpl userValidator, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto save(UserDto userDto) {
        userValidator.validateAllFields(userDto);

        User newUser = userMapper.mapToUser(userDto);
        User savedUser = userJpaRepository.save(newUser);

        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto findById(long id) {
        User user = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("user with id: %s not found", id)));
        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateById(long userId, UserDto userDto) {
        User userById = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("user with id: %s not found", userId)));

        userValidator.validateNonNullFields(userDto);

        User user = userMapper.mapToUser(userDto, userById);
        user.setId(userId);
        User updatedUser = userJpaRepository.save(user);

        return userMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteById(long userId) {
        userJpaRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userJpaRepository.findAll();
        return users.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
