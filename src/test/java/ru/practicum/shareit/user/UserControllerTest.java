package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ErrorHandler.class})
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserServiceImpl userService;

    @Test
    void findAll_ReturnOk() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "email");
        List<UserDto> dtoList = List.of(userDto);
        String expectedContent = objectMapper.writeValueAsString(dtoList);

        when(userService.findAll()).thenReturn(dtoList);

        String responseContent = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedContent, responseContent);
    }

    @Test
    void findById_ReturnOk() throws Exception {
        long userId = 1L;

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).findById(userId);
    }

    @Test
    void findById_ThrownResourceNotFound() throws Exception {
        long userId = 1L;
        when(userService.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void save_ReturnOk() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "email");
        String requestBody = objectMapper.writeValueAsString(userDto);

        when(userService.save(userDto)).thenReturn(userDto);

        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(response, requestBody);
    }

    @Test
    void update_ReturnOk() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto(1L, "name", "email");
        String requestBody = objectMapper.writeValueAsString(userDto);

        when(userService.updateById(userId, userDto)).thenReturn(userDto);

        String response = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(requestBody, response);
    }

    @Test
    void deleteById_ReturnOk() throws Exception {
        long userId = 1L;

        String response = mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(response, String.format("user with id: %s deleted", userId));
        verify(userService, times(1)).deleteById(userId);
    }
}