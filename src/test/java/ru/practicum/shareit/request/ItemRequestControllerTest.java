package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestValidator requestValidator;
    @MockBean
    private RequestServiceImpl requestService;

    private final ResponseDto responseDto = new ResponseDto(1L, "description", LocalDateTime.now(), null);

    private final long userId = 1L;
    private final RequestDto requestDto = new RequestDto("description");

    @Test
    void add_ReturnOk() throws Exception {
        when(requestService.create(anyLong(), any(RequestDto.class))).thenReturn(responseDto);

        String response = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestValidator, Mockito.times(1)).validateAllFields(any(RequestDto.class));
        assertEquals(objectMapper.writeValueAsString(responseDto), response);
    }

    @Test
    void findAllByUserId_ReturnOK() throws Exception {
        when(requestService.findAllByUserId(userId)).thenReturn(List.of(responseDto));

        String response = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(responseDto)), response);

    }

    @Test
    void findOneByUserId_ReturnOk() throws Exception {
        when(requestService.findOneByRequestId(anyLong(), anyLong())).thenReturn(responseDto);

        String response = mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDto), response);
    }

    @Test
    void findAll_ReturnOk() throws Exception {
        when(requestService.findAll(userId, 1, 1)).thenReturn(List.of(responseDto));

        String response = mockMvc.perform(get("/requests/all?from=1&size=1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(responseDto)), response);
    }
}