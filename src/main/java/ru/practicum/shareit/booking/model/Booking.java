package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public Booking(long id, LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public Booking() {
    }

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class BookingBuilder {
        private long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Item item;
        private User booker;
        private Status status;

        BookingBuilder() {
        }

        public BookingBuilder id(long id) {
            this.id = id;
            return this;
        }

        public BookingBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public BookingBuilder end(LocalDateTime end) {
            this.end = end;
            return this;
        }

        public BookingBuilder item(Item item) {
            this.item = item;
            return this;
        }

        public BookingBuilder booker(User booker) {
            this.booker = booker;
            return this;
        }

        public BookingBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public Booking build() {
            return new Booking(id, start, end, item, booker, status);
        }

        public String toString() {
            return "Booking.BookingBuilder(id=" + this.id + ", start=" + this.start + ", end=" + this.end + ", item=" + this.item + ", booker=" + this.booker + ", status=" + this.status + ")";
        }
    }
}
