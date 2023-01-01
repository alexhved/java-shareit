package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    UserMapper userMapper = new UserMapper();
    UserDto userDto = new UserDto(1, "Ronny", "ggg@mail.com");
    User user = new User(1, "Ronny", "ggg@mail.com");

    @Test
    void mapToUser() {
        User newUser = userMapper.mapToUser(userDto);

        assertThat(newUser)
                .hasFieldOrPropertyWithValue("id", userDto.getId())
                .hasFieldOrPropertyWithValue("name", userDto.getName())
                .hasFieldOrPropertyWithValue("email", userDto.getEmail());
    }

    @Test
    void testMapToUser() {
        User newUser = userMapper.mapToUser(userDto, user);

        assertThat(newUser)
                .hasFieldOrPropertyWithValue("id", userDto.getId())
                .hasFieldOrPropertyWithValue("name", userDto.getName())
                .hasFieldOrPropertyWithValue("email", userDto.getEmail());
    }

    @Test
    void mapToUserDto() {
        UserDto userDto1 = userMapper.mapToUserDto(user);

        assertThat(userDto1)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());
    }
}