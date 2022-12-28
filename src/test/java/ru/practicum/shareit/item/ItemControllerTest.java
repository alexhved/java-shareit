package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class, ErrorHandler.class})
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemServiceImpl itemService;

    ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "name", "description", true, 1L);
    long userId = 1L;
    long itemId = 2L;
    ItemResponseDto itemResponseDto = new ItemResponseDto();


    @Test
    void save_ReturnOk() throws Exception {
        when(itemService.save(userId, itemRequestDto)).thenReturn(itemResponseDto);

        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), response);
    }

    @Test
    void update_ReturnOk() throws Exception {
        when(itemService.update(userId, itemId, itemRequestDto)).thenReturn(itemResponseDto);

        String response = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), response);
    }

    @Test
    void findById_ReturnOk() throws Exception {
        when(itemService.findById(userId, itemId)).thenReturn(itemResponseDto);

        String response = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), response);
    }

    @Test
    void findById_ThenThrowMissingResourceAccess() throws Exception {
        when(itemService.findById(userId, itemId)).thenThrow(ResourceAccessException.class);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findById_ThenThrowMissingResourceNotFound() throws Exception {
        when(itemService.findById(userId, itemId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByUserId_ReturnOk() throws Exception {
        int from = 1;
        int size = 1;
        List<ItemResponseDto> dtoList = List.of(itemResponseDto);

        when(itemService.findAllByUserId(userId, from, size)).thenReturn(dtoList);

        String response = mockMvc.perform(get("/items?from=1&size=1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), response);
    }

    @Test
    void findAllByUserId_ThenThrowMissingResourceAccess() throws Exception {
        when(itemService.findById(userId, itemId)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchItem_ReturnOk() throws Exception {
        int from = 1;
        int size = 1;
        String text = "text";
        List<ItemResponseDto> dtoList = List.of(itemResponseDto);

        when(itemService.searchItem(userId, text, from, size)).thenReturn(dtoList);

        String response = mockMvc.perform(get("/items/search?text=text&from=1&size=1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), response);
    }

    @Test
    void addComment_ReturnOk() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder().build();

        when(itemService.addComment(userId, itemId, commentRequestDto)).thenReturn(commentResponseDto);

        String response = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentResponseDto), response);

    }
}