package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;
    private final UserValidator userValidator;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@Min(1) @PathVariable long userId) {
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserDto user) {
        return userClient.save(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Min(1) @PathVariable long userId,
                                         @RequestBody UserDto userDto) {

        userValidator.validateNonNullFields(userDto);
        return userClient.updateById(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Min(1) @PathVariable long userId) {
        return userClient.deleteById(userId);
    }
}
