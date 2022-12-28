package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.error.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository mockUserRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    UserValidator mockUserValidator;

    @InjectMocks
    UserServiceImpl userService;

    UserDto userDto;
    UserDto userDto2;

    User user;

    User user2;


    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "Ronny", "email@.com");
        userDto2 = new UserDto(2, "Jonny", "jmail@.com");
        user = new User(1, "Ronny", "email@.com");
        user2 = new User(2, "Jonny", "jmail@.com");
    }

    @Test
    void save_thanReturnUser() {
        when(userMapper.mapToUser(userDto)).thenReturn(user);
        when(mockUserRepository.save(user)).thenReturn(user);
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDto saved = userService.save(userDto);

        assertThat(userDto).isEqualTo(saved);
        Mockito.verify(mockUserValidator, Mockito.times(1)).validateAllFields(userDto);

    }

    @Test
    void save_thanThrow() {
        doThrow(ValidateException.class).when(mockUserValidator).validateAllFields(userDto);

        assertThrows(ValidateException.class, () -> userService.save(userDto));
        verify(userMapper, never()).mapToUser(userDto);
        verify(userMapper, never()).mapToUserDto(user);
        verify(mockUserRepository, never()).save(user);
    }

    @Test
    void findById_thanReturnUser() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDto userDto1 = userService.findById(1);

        assertThat(userDto1).isEqualTo(userDto);
    }

    @Test
    void findById_thanThrow() {
        when(mockUserRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(0L));
    }


    @Test
    void updateById_thanReturnUser() {
        when(userMapper.mapToUser(userDto, user)).thenReturn(user);
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockUserRepository.save(user)).thenReturn(user);
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDto userDtoUpdated = userService.updateById(1L, userDto);

        assertThat(1).isEqualTo(userDtoUpdated.getId());
        Mockito.verify(mockUserValidator, Mockito.times(1)).validateNonNullFields(userDto);
    }

    @Test
    void updateById_thanThrow() {
        when(mockUserRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(0L));
        Mockito.verify(mockUserValidator, never()).validateNonNullFields(userDto);
        verify(mockUserValidator, never()).validateNonNullFields(userDto);
    }

    @Test
    void deleteById_ShouldWork() {
        userService.deleteById(1L);

        verify(mockUserRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findAll_ReturnDtoList() {
        List<User> users = new ArrayList<>();

        when(mockUserRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.findAll();

        assertThat(users.size()).isEqualTo(userDtos.size());
    }
}