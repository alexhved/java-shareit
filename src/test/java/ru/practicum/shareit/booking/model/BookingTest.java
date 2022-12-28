package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void getId() {
        long bookingId = 1L;
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), null, null, null);
        long id = booking.getId();
        assertEquals(bookingId, id);
    }

    @Test
    void equals() {
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), null, null, null);
        Booking booking2 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), null, null, null);
        boolean equals = booking.equals(booking2);
        assertTrue(equals);
    }

}