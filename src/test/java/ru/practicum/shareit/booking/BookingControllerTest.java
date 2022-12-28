package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.error.BookingStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    private final long userId = 1L;
    private final String xSharerUserId = "X-Sharer-User-Id";

    private final BookingRequestDto bookingRequestDto =
            new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, Status.WAITING);

    private final BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

    private final long bookingId = 1L;

    @Test
    void create() throws Exception {
        when(bookingService.create(userId, bookingRequestDto)).thenReturn(bookingResponseDto);

        String response = mockMvc.perform(post("/bookings")
                        .header(xSharerUserId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResponseDto), response);
    }

    @Test
    void approve() throws Exception {
        boolean approved = true;

        when(bookingService.approve(userId, bookingId, approved)).thenReturn(bookingResponseDto);

        String response = mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResponseDto), response);
    }

    @Test
    void approve_ThenThrowBookingStatusException() throws Exception {
        boolean approved = true;

        when(bookingService.approve(userId, bookingId, approved)).thenThrow(BookingStatusException.class);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(xSharerUserId, userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getByUserOrOwner() throws Exception {
        when(bookingService.getByBookerOrOwner(userId, bookingId)).thenReturn(bookingResponseDto);

        String response = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResponseDto), response);
    }

    @Test
    void findAllByBookerIdAndState() throws Exception {
        List<BookingResponseDto> dtoList = List.of(bookingResponseDto);
        int from = 1;
        int size = 1;
        String state = "ALL";

        when(bookingService.findAllByBookerIdAndState(userId, state, from, size))
                .thenReturn(dtoList);

        String response = mockMvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(dtoList), response);
    }

    @Test
    void findAllByOwnerIdAndState() throws Exception {
        List<BookingResponseDto> dtoList = List.of(bookingResponseDto);
        int from = 1;
        int size = 1;
        String state = "ALL";

        when(bookingService.findAllByOwnerIdAndState(userId, state, from, size))
                .thenReturn(dtoList);

        String response = mockMvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(dtoList), response);
    }
}